package com.processout.example.ui.screen.checkout

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.example.R
import com.processout.example.databinding.FragmentAuthorizeInvoiceBinding
import com.processout.example.service.threeds.Checkout3DSServiceDelegate
import com.processout.example.service.threeds.POAdyen3DSService
import com.processout.example.shared.Constants
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.screen.checkout.DynamicCheckoutUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.checkout.threeds.POCheckout3DSService
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration
import com.processout.sdk.ui.checkout.PODynamicCheckoutLauncher
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import kotlinx.coroutines.launch

class DynamicCheckoutFragment : BaseFragment<FragmentAuthorizeInvoiceBinding>(
    FragmentAuthorizeInvoiceBinding::inflate
) {

    private val viewModel: DynamicCheckoutViewModel by viewModels {
        DynamicCheckoutViewModel.Factory()
    }

    private lateinit var launcher: PODynamicCheckoutLauncher
    private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = PODynamicCheckoutLauncher.create(
            from = this,
            delegate = DefaultDynamicCheckoutDelegate(
                invoices = ProcessOut.instance.invoices
            ),
            callback = ::handle
        )
        customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { handle(it) }
            }
        }
    }

    private fun handle(result: ProcessOutActivityResult<POUnit>) {
        viewModel.reset()
        result.onSuccess {
            showAlert(getString(R.string.success))
        }.onFailure {
            showAlert(it.toMessage())
        }
    }

    private fun handle(uiState: DynamicCheckoutUiState) {
        handleControls(uiState)
        when (uiState) {
            is Submitted -> launchDynamicCheckout(uiState.uiModel.invoiceId)
            is Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun launchDynamicCheckout(invoiceId: String) {
        launcher.launch(
            PODynamicCheckoutConfiguration(
                invoiceRequest = POInvoiceRequest(
                    invoiceId = invoiceId
                ),
                returnUrl = Constants.RETURN_URL
            )
        )
        viewModel.onLaunched()
    }

    private fun create3DSService(): PO3DSService? {
        val selected3DSService = with(binding.threedsServiceRadioGroup) {
            findViewById<RadioButton>(checkedRadioButtonId).text.toString()
        }
        return when (selected3DSService) {
            getString(R.string.threeds_service_checkout) -> createCheckout3DSService()
            getString(R.string.threeds_service_adyen) -> createAdyen3DSService()
            else -> null
        }
    }

    private fun createCheckout3DSService(): PO3DSService =
        POCheckout3DSService.Builder(
            activity = requireActivity(),
            delegate = Checkout3DSServiceDelegate(
                activity = requireActivity(),
                customTabLauncher = customTabLauncher,
                returnUrl = Constants.RETURN_URL
            )
        ).build()

    private fun createAdyen3DSService(): PO3DSService =
        POAdyen3DSService(
            activity = requireActivity(),
            customTabLauncher = customTabLauncher,
            returnUrl = Constants.RETURN_URL
        )

    private fun setOnClickListeners() {
        binding.authorizeInvoiceButton.setOnClickListener { onSubmitClick() }
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
        with(binding) {
            val details = InvoiceDetails(
                amount = amountInput.text.toString(),
                currency = currencyInput.text.toString()
            )
            viewModel.submit(details)
        }
    }

    private fun handleControls(uiState: DynamicCheckoutUiState) {
        when (uiState) {
            Submitting -> enableControls(false)
            else -> enableControls(true)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        with(binding) {
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            authorizeInvoiceButton.isClickable = isEnabled
            amountInput.clearFocus()
            currencyInput.clearFocus()
        }
    }

    private fun showAlert(message: String) {
        POAlertDialog(
            context = requireContext(),
            title = getString(R.string.dynamic_checkout),
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
