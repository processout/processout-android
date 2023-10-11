package com.processout.example.ui.screen.card

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.checkout.threeds.Environment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.processout.example.R
import com.processout.example.databinding.FragmentCardPaymentBinding
import com.processout.example.service.Checkout3DSServiceDelegate
import com.processout.example.service.POAdyen3DSService
import com.processout.example.shared.Constants
import com.processout.example.shared.onFailure
import com.processout.example.shared.onSuccess
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.screen.card.CardPaymentUiState.Authorizing
import com.processout.example.ui.screen.card.CardPaymentUiState.Failure
import com.processout.example.ui.screen.card.CardPaymentUiState.Submitted
import com.processout.example.ui.screen.card.CardPaymentUiState.Submitting
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.checkout.threeds.POCheckout3DSService
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import com.processout.sdk.ui.threeds.POTest3DSService
import kotlinx.coroutines.launch

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>(
    FragmentCardPaymentBinding::inflate
) {

    private val viewModel: CardPaymentViewModel by viewModels {
        CardPaymentViewModel.Factory()
    }

    private val invoices = ProcessOut.instance.invoices
    private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { handle(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                invoices.authorizeInvoiceResult.collect { onAuthorizeInvoiceResult(it) }
            }
        }
    }

    private fun authorizeInvoice(invoiceId: String, cardId: String) {
        invoices.authorizeInvoice(
            request = POInvoiceAuthorizationRequest(
                invoiceId = invoiceId,
                source = cardId
            ),
            threeDSService = create3DSService()
        )
        viewModel.onAuthorizing()
    }

    private fun onAuthorizeInvoiceResult(result: ProcessOutResult<String>) {
        val uiState = viewModel.uiState.value
        val cardId = if (uiState is Authorizing) uiState.uiModel.cardId else null
        viewModel.reset()
        result.onSuccess { showAlert(getString(R.string.authorize_invoice_success_format, it, cardId)) }
            .onFailure { showAlert(it.toMessage()) }
    }

    private fun create3DSService(): PO3DSService {
        val selected3DSService = with(binding.threedsServiceRadioGroup) {
            findViewById<RadioButton>(checkedRadioButtonId).text.toString()
        }
        return when (selected3DSService) {
            getString(R.string.threeds_service_checkout) -> createCheckout3DSService()
            getString(R.string.threeds_service_adyen) -> createAdyen3DSService()
            else -> createTest3DSService()
        }
    }

    private fun createTest3DSService(): PO3DSService =
        POTest3DSService(
            activity = requireActivity(),
            customTabLauncher = customTabLauncher,
            returnUrl = Constants.RETURN_URL
        )

    private fun createCheckout3DSService(): PO3DSService =
        POCheckout3DSService.Builder(
            activity = requireActivity(),
            delegate = Checkout3DSServiceDelegate(
                activity = requireActivity(),
                customTabLauncher = customTabLauncher,
                returnUrl = Constants.RETURN_URL
            )
        )   // Optional parameter, by default Environment.PRODUCTION
            .with(environment = Environment.PRODUCTION)
            .build()

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
            val details = CardPaymentDetails(
                card = CardDetails(
                    number = numberInput.text.toString(),
                    expMonth = expMonthInput.text.toString().let {
                        if (it.isNotBlank() && it.isDigitsOnly()) it else "0"
                    },
                    expYear = expYearInput.text.toString().let {
                        if (it.isNotBlank() && it.isDigitsOnly()) it else "0"
                    },
                    cvc = cvcInput.text.toString()
                ),
                invoice = InvoiceDetails(
                    amount = amountInput.text.toString(),
                    currency = currencyInput.text.toString()
                )
            )
            viewModel.submit(details)
        }
    }

    private fun handle(uiState: CardPaymentUiState) {
        handleControls(uiState)
        when (uiState) {
            is Submitted -> with(uiState.uiModel) {
                authorizeInvoice(invoiceId, cardId)
            }
            is Failure -> showAlert(uiState.failure.toMessage())
            else -> {}
        }
    }

    private fun handleControls(uiState: CardPaymentUiState) {
        when (uiState) {
            Submitting -> enableControls(false)
            else -> enableControls(true)
        }
    }

    private fun enableControls(isEnabled: Boolean) {
        with(binding) {
            numberInput.isEnabled = isEnabled
            expMonthInput.isEnabled = isEnabled
            expYearInput.isEnabled = isEnabled
            cvcInput.isEnabled = isEnabled
            amountInput.isEnabled = isEnabled
            currencyInput.isEnabled = isEnabled
            authorizeInvoiceButton.isClickable = isEnabled
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
