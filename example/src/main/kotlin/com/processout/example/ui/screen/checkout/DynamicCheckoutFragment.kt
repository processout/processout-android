package com.processout.example.ui.screen.checkout

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.example.R
import com.processout.example.databinding.FragmentDynamicCheckoutBinding
import com.processout.example.service.threeds.Checkout3DSServiceDelegate
import com.processout.example.shared.Constants
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.MainActivity
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
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.AlternativePaymentConfiguration
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.AlternativePaymentConfiguration.PaymentConfirmationConfiguration
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.SubmitButton
import com.processout.sdk.ui.checkout.PODynamicCheckoutLauncher
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import kotlinx.coroutines.launch

class DynamicCheckoutFragment : BaseFragment<FragmentDynamicCheckoutBinding>(
    FragmentDynamicCheckoutBinding::inflate
) {

    private val viewModel: DynamicCheckoutViewModel by viewModels {
        DynamicCheckoutViewModel.Factory()
    }

    private lateinit var launcher: PODynamicCheckoutLauncher
    private lateinit var delegate: DefaultDynamicCheckoutDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate = DefaultDynamicCheckoutDelegate(
            invoices = ProcessOut.instance.invoices
        )
        launcher = PODynamicCheckoutLauncher.create(
            from = this,
            threeDSService = createCheckout3DSService(
                customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
            ),
            delegate = delegate,
            callback = ::handle
        )
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
            is Submitted -> {
                delegate.customerId = uiState.uiModel.customerId
                launchDynamicCheckout(uiState.uiModel)
            }
            is Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun launchDynamicCheckout(uiModel: DynamicCheckoutUiModel) {
        launcher.launch(
            PODynamicCheckoutConfiguration(
                invoiceRequest = POInvoiceRequest(
                    invoiceId = uiModel.invoiceId,
                    clientSecret = uiModel.clientSecret
                ),
                alternativePayment = AlternativePaymentConfiguration(
                    returnUrl = Constants.RETURN_URL,
                    paymentConfirmation = PaymentConfirmationConfiguration(
                        confirmButton = SubmitButton()
                    )
                )
            )
        )
        viewModel.onLaunched()
    }

    private fun createCheckout3DSService(
        customTabLauncher: PO3DSRedirectCustomTabLauncher
    ): PO3DSService = POCheckout3DSService.Builder(
        activity = requireActivity(),
        delegate = Checkout3DSServiceDelegate(
            activity = requireActivity(),
            customTabLauncher = customTabLauncher,
            returnUrl = Constants.RETURN_URL
        )
    ).build()

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
            Initial -> enableControls(true)
            else -> enableControls(false)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        (requireActivity() as MainActivity).adjustImeInsets = isEnabled
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
