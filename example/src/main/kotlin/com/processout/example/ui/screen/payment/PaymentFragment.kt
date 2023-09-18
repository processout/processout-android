package com.processout.example.ui.screen.payment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.processout.example.R
import com.processout.example.databinding.FragmentPaymentBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.core.POFailure
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
                binding.resultTextView.text = getString(R.string.result_success)
            is PONativeAlternativePaymentMethodResult.Failure -> {
                when (result.code) {
                    POFailure.Code.Cancelled ->
                        binding.resultTextView.text = getString(R.string.result_canceled)
                    else -> binding.resultTextView.text = getString(R.string.result_failure)
                }
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

    private fun handle(uiState: PaymentUiState) {
        when (uiState) {
            is PaymentUiState.Submitted -> startNativeAPM(uiState.uiModel)
            is PaymentUiState.Failure -> binding.resultTextView.text = uiState.failure.toString()
            else -> {}
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
        binding.resultTextView.text = String()
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
