package com.processout.example.ui.screen.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.processout.example.R
import com.processout.example.databinding.FragmentPaymentBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodLauncher
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResultCallback
import kotlinx.coroutines.launch
import java.text.NumberFormat

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
        launcher = PONativeAlternativePaymentMethodLauncher.create(this, callback)
    }

    private val callback = object : PONativeAlternativePaymentMethodResultCallback {
        override fun onNativeAlternativePaymentMethodResult(result: PONativeAlternativePaymentMethodResult) {
            when (result) {
                PONativeAlternativePaymentMethodResult.Success ->
                    binding.resultTextView.text = getString(R.string.result_success)
                is PONativeAlternativePaymentMethodResult.Failure ->
                    binding.resultTextView.text = getString(R.string.result_failure)
                PONativeAlternativePaymentMethodResult.Canceled ->
                    binding.resultTextView.text = getString(R.string.result_canceled)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = args.title

        binding.createInvoiceButton.setOnClickListener {
            binding.resultTextView.text = String()
            val amount = binding.amountInput.text.toString()
            val currency = binding.currencyInput.text.toString()
            viewModel.createInvoice(amount, currency)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                startNativeAPM(it)
            }
        }
    }

    private fun startNativeAPM(uiModel: PaymentUiModel) {
        launcher.launch(
            PONativeAlternativePaymentMethodConfiguration(
                gatewayConfigurationId = uiModel.gatewayConfigurationId,
                invoiceId = uiModel.invoiceId,
                viewType = PONativeAlternativePaymentMethodConfiguration.ViewType.BOTTOM_SHEET,
                options = PONativeAlternativePaymentMethodConfiguration.Options(
                    title = getString(R.string.app_name),
                    currencyFormat = NumberFormat.getCurrencyInstance(),
                    isBottomSheetCancelableOnOutsideTouch = true
                )
            )
        )
    }
}
