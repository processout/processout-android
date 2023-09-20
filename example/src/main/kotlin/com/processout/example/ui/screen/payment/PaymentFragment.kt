package com.processout.example.ui.screen.payment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.processout.example.databinding.FragmentPaymentBinding
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodLauncher
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult
import kotlinx.coroutines.launch

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(
    FragmentPaymentBinding::inflate
) {

    private val args: PaymentFragmentArgs by navArgs()

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.Factory(args.gatewayConfigurationId)
    }

    private lateinit var launcher: PONativeAlternativePaymentMethodLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = PONativeAlternativePaymentMethodLauncher.create(
            this, ::onNativeAlternativePaymentMethodResult
        )
    }

    private fun onNativeAlternativePaymentMethodResult(result: PONativeAlternativePaymentMethodResult) {
        viewModel.reset()
        when (result) {
            PONativeAlternativePaymentMethodResult.Success ->
                binding.resultMessage.text = result.javaClass.simpleName
            is PONativeAlternativePaymentMethodResult.Failure ->
                binding.resultMessage.text = result.toMessage()
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

    private fun handle(uiState: PaymentUiState) {
        handleControls(uiState)
        when (uiState) {
            is PaymentUiState.Submitted -> startNativeAPM(uiState.uiModel)
            is PaymentUiState.Failure -> binding.resultMessage.text = uiState.failure.toMessage()
            else -> {}
        }
    }

    private fun handleControls(uiState: PaymentUiState) {
        when (uiState) {
            PaymentUiState.Submitting -> enableControls(false)
            else -> enableControls(true)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        with(binding) {
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            createInvoiceButton.isClickable = isEnabled
        }
    }

    private fun setOnClickListeners() {
        binding.createInvoiceButton.setOnClickListener { onSubmitClick() }
        binding.currencyInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSubmitClick()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }
    }

    private fun onSubmitClick() {
        binding.resultMessage.text = String()
        val amount = binding.amountInput.text.toString()
        val currency = binding.currencyInput.text.toString()
        viewModel.createInvoice(amount, currency)
    }

    private fun startNativeAPM(uiModel: PaymentUiModel) {
        launcher.launch(
            PONativeAlternativePaymentMethodConfiguration(
                gatewayConfigurationId = uiModel.gatewayConfigurationId,
                invoiceId = uiModel.invoiceId
            )
        )
    }
}
