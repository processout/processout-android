package com.processout.sdk.ui.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.R
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.checkout.DynamicCheckoutActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Failure
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Success
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.Dismiss
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration

internal class DynamicCheckoutActivity : BaseTransparentPortraitActivity() {

    private var configuration: PODynamicCheckoutConfiguration? = null

    private val viewModel: DynamicCheckoutViewModel by viewModels {
        val cardTokenization: CardTokenizationViewModel by viewModels {
            CardTokenizationViewModel.Factory(
                app = application,
                configuration = POCardTokenizationConfiguration()
            )
        }
        val nativeAlternativePayment: NativeAlternativePaymentViewModel by viewModels {
            NativeAlternativePaymentViewModel.Factory(
                app = application,
                invoiceId = configuration?.invoiceRequest?.invoiceId ?: String(),
                gatewayConfigurationId = String(),
                options = PONativeAlternativePaymentConfiguration.Options(
                    paymentConfirmation = PaymentConfirmationConfiguration(
                        hideGatewayDetails = true
                    ),
                    skipSuccessScreen = true
                )
            )
        }
        DynamicCheckoutViewModel.Factory(
            app = application,
            invoiceRequest = configuration?.invoiceRequest ?: POInvoiceRequest(invoiceId = String()),
            options = configuration?.options ?: PODynamicCheckoutConfiguration.Options(),
            cardTokenization = cardTokenization,
            nativeAlternativePayment = nativeAlternativePayment
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        if (savedInstanceState == null) {
            initConfiguration()
        }
        dispatchBackPressed()
        setContent {
            ProcessOutTheme {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                DynamicCheckoutScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    style = DynamicCheckoutScreen.style(custom = configuration?.style)
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
