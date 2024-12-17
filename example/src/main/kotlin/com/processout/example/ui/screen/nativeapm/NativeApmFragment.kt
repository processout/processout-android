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
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.MainActivity
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.screen.nativeapm.NativeApmUiState.*
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentLauncher
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

    private lateinit var launcher: PONativeAlternativePaymentMethodLauncher
    private lateinit var launcherCompose: PONativeAlternativePaymentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = PONativeAlternativePaymentMethodLauncher.create(from = this) { result ->
            viewModel.reset()
            when (result) {
                PONativeAlternativePaymentMethodResult.Success ->
                    showAlert(getString(R.string.success))
                is PONativeAlternativePaymentMethodResult.Failure ->
                    showAlert(result.toMessage())
            }
        }
        launcherCompose = PONativeAlternativePaymentLauncher.create(from = this) { result ->
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
        binding.launchNativeApmButton.setOnClickListener {
            onSubmitClick(launchCompose = false)
        }
        binding.launchNativeApmComposeButton.setOnClickListener {
            onSubmitClick(launchCompose = true)
        }
    }

    private fun onSubmitClick(launchCompose: Boolean) {
        val amount = binding.amountInput.text.toString()
        val currency = binding.currencyInput.text.toString()
        viewModel.createInvoice(amount, currency, launchCompose)
    }

    private fun handle(uiState: NativeApmUiState) {
        handleControls(uiState)
        when (uiState) {
            is Submitted -> launch(uiState.uiModel)
            is Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun launch(uiModel: NativeApmUiModel) {
        if (uiModel.launchCompose) {
            launcherCompose.launch(
                PONativeAlternativePaymentConfiguration(
                    invoiceId = uiModel.invoiceId,
                    gatewayConfigurationId = uiModel.gatewayConfigurationId
                )
            )
        } else {
            launcher.launch(
                PONativeAlternativePaymentMethodConfiguration(
                    gatewayConfigurationId = uiModel.gatewayConfigurationId,
                    invoiceId = uiModel.invoiceId
                )
            )
        }
        viewModel.onLaunched()
    }

    private fun handleControls(uiState: NativeApmUiState) {
        when (uiState) {
            Initial -> enableControls(true)
            else -> enableControls(false)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        (requireActivity() as MainActivity).adjustImeInsets = isEnabled
        with(binding) {
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            launchNativeApmButton.isClickable = isEnabled
            launchNativeApmComposeButton.isClickable = isEnabled
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
