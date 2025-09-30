package com.processout.example.ui.screen.nativeapm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.processout.example.R
import com.processout.example.databinding.FragmentNativeApmBinding
import com.processout.example.shared.Constants
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.MainActivity
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.screen.nativeapm.NativeApmFlow.*
import com.processout.example.ui.screen.nativeapm.NativeApmUiState.*
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentLauncher
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodLauncher
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog
import kotlinx.coroutines.launch

class NativeApmFragment : BaseFragment<FragmentNativeApmBinding>(
    FragmentNativeApmBinding::inflate
) {

    private val args: NativeApmFragmentArgs by navArgs()

    private val viewModel: NativeApmViewModel by viewModels {
        NativeApmViewModel.Factory(args.gatewayConfigurationId)
    }

    private lateinit var launcherLegacy: PONativeAlternativePaymentMethodLauncher
    private lateinit var launcherCompose: PONativeAlternativePaymentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcherLegacy = PONativeAlternativePaymentMethodLauncher.create(from = this) { result ->
            viewModel.reset()
            when (result) {
                PONativeAlternativePaymentMethodResult.Success ->
                    showAlert(getString(R.string.success))
                is PONativeAlternativePaymentMethodResult.Failure ->
                    showAlert(result.toMessage())
            }
        }
        launcherCompose = PONativeAlternativePaymentLauncher.create(
            from = this,
            delegate = object : PONativeAlternativePaymentDelegate {}
        ) { result ->
            viewModel.reset()
            result.onSuccess {
                showAlert(getString(R.string.success))
            }.onFailure { failure ->
                showAlert(failure.toMessage())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { handle(it) }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.buttonAuthorize.setOnClickListener {
            onSubmitClick(flow = AUTHORIZE)
        }
        binding.buttonAuthorizeCustomerToken.setOnClickListener {
            onSubmitClick(flow = AUTHORIZE_CUSTOMER_TOKEN)
        }
        binding.buttonTokenize.setOnClickListener {
            onSubmitClick(flow = TOKENIZE)
        }
        binding.buttonAuthorizeLegacy.setOnClickListener {
            onSubmitClick(flow = AUTHORIZE_LEGACY)
        }
    }

    private fun onSubmitClick(flow: NativeApmFlow) {
        val amount = binding.amountInput.text.toString()
        val currency = binding.currencyInput.text.toString()
        viewModel.createInvoice(
            amount = amount,
            currency = currency,
            flow = flow
        )
    }

    private fun handle(uiState: NativeApmUiState) {
        binding.customer.text = viewModel.customerId
        binding.customerToken.text = viewModel.customerTokenId
        handleControls(uiState)
        when (uiState) {
            is Submitted -> launch(uiState.uiModel)
            is Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun launch(uiModel: NativeApmUiModel) {
        when (uiModel.flow) {
            AUTHORIZE -> launcherCompose.launch(
                PONativeAlternativePaymentConfiguration(
                    flow = Flow.Authorization(
                        invoiceId = uiModel.invoiceId,
                        gatewayConfigurationId = uiModel.gatewayConfigurationId
                    ),
                    cancelButton = CancelButton(),
                    redirect = RedirectConfiguration(
                        returnUrl = Constants.RETURN_URL
                    ),
                    paymentConfirmation = PaymentConfirmationConfiguration(
                        confirmButton = Button(),
                        cancelButton = CancelButton(disabledForSeconds = 3)
                    )
                )
            )
            AUTHORIZE_CUSTOMER_TOKEN -> launcherCompose.launch(
                PONativeAlternativePaymentConfiguration(
                    flow = Flow.Authorization(
                        invoiceId = uiModel.invoiceId,
                        gatewayConfigurationId = uiModel.gatewayConfigurationId,
                        customerTokenId = uiModel.customerTokenId
                    ),
                    cancelButton = CancelButton(),
                    redirect = RedirectConfiguration(
                        returnUrl = Constants.RETURN_URL,
                        enableHeadlessMode = true
                    ),
                    paymentConfirmation = PaymentConfirmationConfiguration(
                        confirmButton = Button(),
                        cancelButton = CancelButton(disabledForSeconds = 3)
                    )
                )
            )
            AUTHORIZE_LEGACY -> launcherLegacy.launch(
                PONativeAlternativePaymentMethodConfiguration(
                    gatewayConfigurationId = uiModel.gatewayConfigurationId,
                    invoiceId = uiModel.invoiceId
                )
            )
            TOKENIZE -> launcherCompose.launch(
                PONativeAlternativePaymentConfiguration(
                    flow = Flow.Tokenization(
                        customerId = uiModel.customerId,
                        customerTokenId = uiModel.customerTokenId,
                        gatewayConfigurationId = uiModel.gatewayConfigurationId
                    ),
                    cancelButton = CancelButton(),
                    redirect = RedirectConfiguration(
                        returnUrl = Constants.RETURN_URL
                    ),
                    paymentConfirmation = PaymentConfirmationConfiguration(
                        confirmButton = Button(),
                        cancelButton = CancelButton(disabledForSeconds = 3)
                    )
                )
            )
        }
        viewModel.onLaunched()
    }

    private fun handleControls(uiState: NativeApmUiState) {
        when (uiState) {
            Initial, is Failure -> enableControls(true)
            else -> enableControls(false)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        (requireActivity() as MainActivity).adjustImeInsets = isEnabled
        with(binding) {
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            buttonAuthorize.isClickable = isEnabled
            buttonAuthorizeCustomerToken.isClickable = isEnabled
            buttonTokenize.isClickable = isEnabled
            buttonAuthorizeLegacy.isClickable = isEnabled
            amountInput.clearFocus()
            currencyInput.clearFocus()
        }
    }

    private fun showAlert(message: String) {
        POAlertDialog(
            context = requireContext(),
            title = getString(R.string.native_apm),
            message = message,
            confirmActionText = getString(R.string.ok),
            dismissActionText = null
        ).onConfirmButtonClick { dialog ->
            dialog.dismiss()
        }.also {
            it.setCancelable(true)
            it.show()
        }
    }
}
