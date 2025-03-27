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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.WalletConstants
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent.DidDeleteCustomerToken
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POGooglePayCardTokenizationData
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.apm.POAlternativePaymentMethodCustomTabLauncher
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.CardScannerConfiguration
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Failure
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Success
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutSideEffect.*
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.*
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.googlepay.POGooglePayCardTokenizationLauncher
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.DEFAULT_TIMEOUT_SECONDS
import com.processout.sdk.ui.savedpaymentmethods.POSavedPaymentMethodsDelegate
import com.processout.sdk.ui.savedpaymentmethods.POSavedPaymentMethodsLauncher
import com.processout.sdk.ui.shared.configuration.POBarcodeConfiguration
import com.processout.sdk.ui.shared.extension.collectImmediately
import com.processout.sdk.ui.web.customtab.POCustomTabAuthorizationActivity
import com.processout.sdk.ui.web.customtab.POCustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivity
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivityContract

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
                configuration = nativeAlternativePaymentConfiguration(),
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
            cardScanner = configuration?.card?.cardScanner?.let {
                CardScannerConfiguration(
                    scanButton = POCardTokenizationConfiguration.Button(
                        text = it.scanButton.text,
                        icon = it.scanButton.icon
                    ),
                    configuration = it.configuration
                )
            },
            billingAddress = BillingAddressConfiguration(
                defaultAddress = billingAddress?.defaultAddress,
                attachDefaultsToPaymentMethod = billingAddress?.attachDefaultsToPaymentMethod ?: false
            ),
            submitButton = configuration?.submitButton?.let {
                POCardTokenizationConfiguration.Button(
                    text = it.text,
                    icon = it.icon
                )
            } ?: POCardTokenizationConfiguration.Button(),
            cancelButton = configuration?.cancelButton?.let {
                POCardTokenizationConfiguration.CancelButton(
                    text = it.text,
                    icon = it.icon,
                    confirmation = it.confirmation
                )
            },
            metadata = configuration?.card?.metadata
        )
    }

    private fun nativeAlternativePaymentConfiguration(): PONativeAlternativePaymentConfiguration {
        val paymentConfirmation = configuration?.alternativePayment?.paymentConfirmation
        return PONativeAlternativePaymentConfiguration(
            invoiceId = configuration?.invoiceRequest?.invoiceId ?: String(),
            gatewayConfigurationId = String(),
            submitButton = configuration?.submitButton?.map() ?: PONativeAlternativePaymentConfiguration.Button(),
            cancelButton = configuration?.cancelButton?.map(),
            paymentConfirmation = PaymentConfirmationConfiguration(
                waitsConfirmation = true,
                timeoutSeconds = paymentConfirmation?.timeoutSeconds ?: DEFAULT_TIMEOUT_SECONDS,
                showProgressIndicatorAfterSeconds = paymentConfirmation?.showProgressIndicatorAfterSeconds,
                hideGatewayDetails = true,
                confirmButton = paymentConfirmation?.confirmButton?.map(),
                cancelButton = paymentConfirmation?.cancelButton?.map()
            ),
            barcode = configuration?.alternativePayment?.barcode
                ?: POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
            inlineSingleSelectValuesLimit = configuration?.alternativePayment?.inlineSingleSelectValuesLimit ?: 5,
            skipSuccessScreen = true
        )
    }

    private fun Button.map() = PONativeAlternativePaymentConfiguration.Button(
        text = text,
        icon = icon
    )

    private fun CancelButton.map() =
        PONativeAlternativePaymentConfiguration.CancelButton(
            text = text,
            icon = icon,
            disabledForSeconds = 0,
            confirmation = confirmation
        )

    private fun AlternativePaymentConfiguration.CancelButton.map() =
        PONativeAlternativePaymentConfiguration.CancelButton(
            text = text,
            icon = icon,
            disabledForSeconds = disabledForSeconds,
            confirmation = confirmation
        )

    private lateinit var googlePayLauncher: POGooglePayCardTokenizationLauncher
    private var pendingGooglePay: GooglePay? = null

    private lateinit var alternativePaymentLauncher: POAlternativePaymentMethodCustomTabLauncher
    private var pendingAlternativePayment: AlternativePayment? = null

    private lateinit var savedPaymentMethodsLauncher: POSavedPaymentMethodsLauncher
    private var pendingSavedPaymentMethods = false

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
            callback = ::handleGooglePay
        )
        alternativePaymentLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
            from = this,
            callback = ::handleAlternativePayment
        )
        savedPaymentMethodsLauncher = POSavedPaymentMethodsLauncher.create(
            from = this,
            delegate = savedPaymentMethodsDelegate,
            callback = { pendingSavedPaymentMethods = false }
        )
        setContent {
            val isLightTheme = !isSystemInDarkTheme()
            ProcessOutTheme(isLightTheme = isLightTheme) {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                viewModel.sideEffects.collectImmediately(
                    minActiveState = Lifecycle.State.CREATED
                ) { handle(it) }

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
                            message = "Invalid configuration: 'invoiceId' is required."
                        )
                    )
                )
            }
        }
    }

    private fun dispatchBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            if (configuration?.cancelOnBackPressed == true) {
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
    }

    private fun handle(sideEffect: DynamicCheckoutSideEffect) {
        when (sideEffect) {
            is GooglePay -> {
                pendingGooglePay = sideEffect
                googlePayLauncher.launch(sideEffect.paymentDataRequest)
            }
            is AlternativePayment -> {
                pendingAlternativePayment = sideEffect
                alternativePaymentLauncher.launch(
                    uri = Uri.parse(sideEffect.redirectUrl),
                    returnUrl = sideEffect.returnUrl
                )
            }
            is SavedPaymentMethods -> {
                pendingSavedPaymentMethods = true
                savedPaymentMethodsLauncher.launch(sideEffect.configuration)
            }
            is PermissionRequest -> requestPermission(sideEffect)
            CancelWebAuthorization -> cancelWebAuthorization()
            BeforeSuccess -> if (pendingSavedPaymentMethods) {
                savedPaymentMethodsLauncher.finish()
            }
        }
    }

    private fun handleGooglePay(result: ProcessOutResult<POGooglePayCardTokenizationData>) {
        pendingGooglePay?.let {
            pendingGooglePay = null
            viewModel.onEvent(
                GooglePayResult(
                    paymentMethodId = it.paymentMethodId,
                    result = result
                )
            )
        }
    }

    private fun handleAlternativePayment(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        pendingAlternativePayment?.let {
            pendingAlternativePayment = null
            viewModel.onEvent(
                AlternativePaymentResult(
                    paymentMethodId = it.paymentMethodId,
                    result = result
                )
            )
        }
    }

    private val savedPaymentMethodsDelegate = object : POSavedPaymentMethodsDelegate {
        override fun onEvent(event: POSavedPaymentMethodsEvent) {
            if (event is DidDeleteCustomerToken) {
                viewModel.onEvent(CustomerTokenDeleted(tokenId = event.tokenId))
            }
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

    private fun cancelWebAuthorization() {
        if (ProcessOut.instance.browserCapabilities.isCustomTabsSupported()) {
            Intent(this, POCustomTabAuthorizationActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                it.putExtra(POCustomTabAuthorizationActivityContract.EXTRA_FORCE_FINISH, true)
                startActivity(it)
            }
        } else {
            Intent(this, POWebViewAuthorizationActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                it.putExtra(POWebViewAuthorizationActivityContract.EXTRA_FORCE_FINISH, true)
                startActivity(it)
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
