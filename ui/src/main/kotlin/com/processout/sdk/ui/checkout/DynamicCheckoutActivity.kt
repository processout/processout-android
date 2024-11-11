package com.processout.sdk.ui.checkout

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.WalletConstants
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.R
import com.processout.sdk.ui.apm.POAlternativePaymentMethodCustomTabLauncher
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Failure
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Success
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.Dismiss
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.PermissionRequestResult
import com.processout.sdk.ui.checkout.DynamicCheckoutSideEffect.*
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.CancelButton
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.googlepay.POGooglePayCardTokenizationLauncher
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.DEFAULT_TIMEOUT_SECONDS
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import com.processout.sdk.ui.shared.extension.collectImmediately

internal class DynamicCheckoutActivity : BaseTransparentPortraitActivity() {

    private var configuration: PODynamicCheckoutConfiguration? = null

    private val viewModel: DynamicCheckoutViewModel by viewModels {
        val cardTokenizationEventDispatcher = PODefaultCardTokenizationEventDispatcher()
        val cardTokenization: CardTokenizationViewModel by viewModels {
            CardTokenizationViewModel.Factory(
                app = application,
                configuration = cardTokenizationConfiguration(),
                eventDispatcher = cardTokenizationEventDispatcher
            )
        }
        val nativeAlternativePaymentEventDispatcher = PODefaultNativeAlternativePaymentMethodEventDispatcher()
        val nativeAlternativePayment: NativeAlternativePaymentViewModel by viewModels {
            NativeAlternativePaymentViewModel.Factory(
                app = application,
                invoiceId = configuration?.invoiceRequest?.invoiceId ?: String(),
                gatewayConfigurationId = String(),
                options = nativeAlternativePaymentConfiguration(),
                eventDispatcher = nativeAlternativePaymentEventDispatcher
            )
        }
        DynamicCheckoutViewModel.Factory(
            app = application,
            configuration = configuration ?: PODynamicCheckoutConfiguration(
                invoiceRequest = POInvoiceRequest(invoiceId = String())
            ),
            cardTokenization = cardTokenization,
            cardTokenizationEventDispatcher = cardTokenizationEventDispatcher,
            nativeAlternativePayment = nativeAlternativePayment,
            nativeAlternativePaymentEventDispatcher = nativeAlternativePaymentEventDispatcher
        )
    }

    private fun cardTokenizationConfiguration(): POCardTokenizationConfiguration {
        val billingAddress = configuration?.card?.billingAddress
        return POCardTokenizationConfiguration(
            billingAddress = BillingAddressConfiguration(
                defaultAddress = billingAddress?.defaultAddress,
                attachDefaultsToPaymentMethod = billingAddress?.attachDefaultsToPaymentMethod ?: false
            ),
            primaryActionText = configuration?.submitButtonText,
            secondaryActionText = configuration?.cancelButton?.text,
            cancellation = POCancellationConfiguration(
                secondaryAction = configuration?.cancelButton != null
            ),
            metadata = configuration?.card?.metadata
        )
    }

    private fun nativeAlternativePaymentConfiguration(): Options {
        val paymentConfirmation = configuration?.alternativePayment?.paymentConfirmation
        return Options(
            primaryActionText = configuration?.submitButtonText,
            secondaryAction = configuration?.cancelButton?.toSecondaryAction(),
            paymentConfirmation = PaymentConfirmationConfiguration(
                waitsConfirmation = true,
                timeoutSeconds = paymentConfirmation?.timeoutSeconds ?: DEFAULT_TIMEOUT_SECONDS,
                showProgressIndicatorAfterSeconds = paymentConfirmation?.showProgressIndicatorAfterSeconds,
                hideGatewayDetails = true,
                primaryAction = paymentConfirmation?.confirmButton?.let { ConfirmAction(text = it.text) },
                secondaryAction = paymentConfirmation?.cancelButton?.toSecondaryAction()
            ),
            inlineSingleSelectValuesLimit = configuration?.alternativePayment?.inlineSingleSelectValuesLimit ?: 5,
            skipSuccessScreen = true
        )
    }

    private fun CancelButton.toSecondaryAction() = SecondaryAction.Cancel(
        text = text,
        disabledForSeconds = disabledForSeconds,
        confirmation = confirmation
    )

    private lateinit var googlePayLauncher: POGooglePayCardTokenizationLauncher
    private lateinit var alternativePaymentLauncher: POAlternativePaymentMethodCustomTabLauncher

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::handlePermissions
    )
    private var pendingPermissionRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        if (savedInstanceState == null) {
            initConfiguration()
        }
        dispatchBackPressed()
        googlePayLauncher = POGooglePayCardTokenizationLauncher.create(
            from = this,
            walletOptions = WalletOptions.Builder()
                .setEnvironment(configuration?.googlePay?.environment?.value ?: WalletConstants.ENVIRONMENT_TEST)
                .build(),
            callback = viewModel::handleGooglePay
        )
        alternativePaymentLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
            from = this,
            callback = viewModel::handleAlternativePayment
        )
        setContent {
            val isLightTheme = !isSystemInDarkTheme()
            ProcessOutTheme(isLightTheme = isLightTheme) {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                viewModel.sideEffects.collectImmediately { handle(it) }
                DynamicCheckoutScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    style = DynamicCheckoutScreen.style(
                        custom = configuration?.style,
                        isLightTheme = isLightTheme
                    ),
                    isLightTheme = isLightTheme
                )
            }
        }
    }

    private fun initConfiguration() {
        @Suppress("DEPRECATION")
        configuration = intent.getParcelableExtra(EXTRA_CONFIGURATION)
        configuration?.run {
            if (invoiceRequest.invoiceId.isBlank()) {
                viewModel.onEvent(
                    Dismiss(
                        ProcessOutResult.Failure(
                            code = Generic(),
                            message = "Invalid configuration."
                        )
                    )
                )
            }
        }
    }

    private fun dispatchBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            viewModel.onEvent(
                Dismiss(
                    ProcessOutResult.Failure(
                        code = Cancelled,
                        message = "Cancelled by the user with back press or gesture."
                    )
                )
            )
        }
    }

    private fun handle(sideEffect: DynamicCheckoutSideEffect) {
        when (sideEffect) {
            is GooglePay -> googlePayLauncher.launch(sideEffect.paymentDataRequest)
            is AlternativePayment -> alternativePaymentLauncher.launch(
                uri = Uri.parse(sideEffect.redirectUrl),
                returnUrl = sideEffect.returnUrl
            )
            is PermissionRequest -> requestPermission(sideEffect)
        }
    }

    private fun requestPermission(request: PermissionRequest) {
        when {
            ContextCompat.checkSelfPermission(
                this, request.permission
            ) == PackageManager.PERMISSION_GRANTED ->
                viewModel.onEvent(
                    PermissionRequestResult(
                        paymentMethodId = request.paymentMethodId,
                        permission = request.permission,
                        isGranted = true
                    )
                )
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, request.permission
            ) -> viewModel.onEvent(
                PermissionRequestResult(
                    paymentMethodId = request.paymentMethodId,
                    permission = request.permission,
                    isGranted = false
                )
            )
            else -> {
                pendingPermissionRequest = request
                permissionsLauncher.launch(arrayOf(request.permission))
            }
        }
    }

    private fun handlePermissions(result: Map<String, Boolean>) {
        pendingPermissionRequest?.let { request ->
            pendingPermissionRequest = null
            result.forEach {
                viewModel.onEvent(
                    PermissionRequestResult(
                        paymentMethodId = request.paymentMethodId,
                        permission = it.key,
                        isGranted = it.value
                    )
                )
            }
        }
    }

    private fun handle(completion: DynamicCheckoutCompletion) =
        when (completion) {
            Success -> finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(POUnit)
            )
            is Failure -> finishWithActivityResult(
                resultCode = Activity.RESULT_CANCELED,
                result = completion.failure.toActivityResult()
            )
            else -> {}
        }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POUnit>
    ) {
        setResult(resultCode, Intent().putExtra(EXTRA_RESULT, result))
        finish()
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, R.anim.po_slide_out_vertical)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, R.anim.po_slide_out_vertical)
        }
    }
}
