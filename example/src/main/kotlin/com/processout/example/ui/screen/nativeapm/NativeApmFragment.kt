package com.processout.example.ui.screen.nativeapm

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.processout.example.R
import com.processout.example.databinding.FragmentNativeApmBinding
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodLauncher
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult
import kotlinx.coroutines.launch

class NativeApmFragment : BaseFragment<FragmentNativeApmBinding>(
    FragmentNativeApmBinding::inflate
) {

    private val args: NativeApmFragmentArgs by navArgs()

    private val viewModel: NativeApmViewModel by viewModels {
        NativeApmViewModel.Factory(args.gatewayConfigurationId)
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
                showAlert(getString(R.string.success))
            is PONativeAlternativePaymentMethodResult.Failure ->
                showAlert(result.toMessage())
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
        val amount = binding.amountInput.text.toString()
        val currency = binding.currencyInput.text.toString()
        viewModel.createInvoice(amount, currency)
    }

    private fun handle(uiState: NativeApmUiState) {
        handleControls(uiState)
        when (uiState) {
            is NativeApmUiState.Submitted -> launch(uiState.uiModel)
            is NativeApmUiState.Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun launch(uiModel: NativeApmUiModel) {
        launcher.launch(
            PONativeAlternativePaymentMethodConfiguration(
                gatewayConfigurationId = uiModel.gatewayConfigurationId,
                invoiceId = uiModel.invoiceId
            )
        )
        viewModel.onLaunched()
    }

    private fun handleControls(uiState: NativeApmUiState) {
        when (uiState) {
            NativeApmUiState.Submitting -> enableControls(false)
            else -> enableControls(true)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        with(binding) {
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            createInvoiceButton.isClickable = isEnabled
            amountInput.clearFocus()
            currencyInput.clearFocus()
        }
    }

    private fun showAlert(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}
