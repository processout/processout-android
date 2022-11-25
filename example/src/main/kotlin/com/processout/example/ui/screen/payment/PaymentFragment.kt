package com.processout.example.ui.screen.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.processout.example.databinding.FragmentPaymentBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.ui.nativeapm.NativeAlternativePaymentMethodActivity
import kotlinx.coroutines.launch

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(
    FragmentPaymentBinding::inflate
) {

    private val args: PaymentFragmentArgs by navArgs()

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.Factory(args.gatewayConfigurationId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = args.title

        binding.createInvoiceButton.setOnClickListener {
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
        startActivity(
            NativeAlternativePaymentMethodActivity.buildStartIntent(
                requireContext(),
                uiModel.gatewayConfigurationId,
                uiModel.invoiceId
            )
        )
    }
}
