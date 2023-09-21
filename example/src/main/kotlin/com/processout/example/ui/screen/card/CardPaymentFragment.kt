package com.processout.example.ui.screen.card

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.example.BuildConfig
import com.processout.example.R
import com.processout.example.databinding.FragmentCardPaymentBinding
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
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import com.processout.sdk.ui.threeds.POTest3DSService
import kotlinx.coroutines.launch

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>(
    FragmentCardPaymentBinding::inflate
) {

    companion object {
        private const val RETURN_URL = "${BuildConfig.APPLICATION_ID}://processout/return"
    }

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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
            threeDSService = createTest3DSService()
        )
        viewModel.onAuthorizing()
    }

    private fun onAuthorizeInvoiceResult(result: ProcessOutResult<String>) {
        val uiState = viewModel.uiState.value
        val cardId = if (uiState is Authorizing) uiState.uiModel.cardId else null
        viewModel.reset()
        with(binding.resultMessage) {
            result.onSuccess { text = getString(R.string.authorize_invoice_success_format, it, cardId) }
                .onFailure { text = it.toMessage() }
        }
    }

    private fun createTest3DSService(): PO3DSService =
        POTest3DSService(requireActivity(), customTabLauncher, RETURN_URL)

    private fun createCheckout3DSService(): PO3DSService = TODO()

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
            resultMessage.text = String()
            val details = CardPaymentDetails(
                card = CardDetails(
                    number = numberInput.text.toString(),
                    expMonth = expMonthInput.text.toString(),
                    expYear = expYearInput.text.toString(),
                    cvc = cvcInput.text.toString()
                ),
                invoice = InvoiceDetails(
                    amount = amountInput.text.toString(),
                    currency = currencyInput.text.toString()
                )
            )
            viewModel.submit(details, RETURN_URL)
        }
    }

    private fun handle(uiState: CardPaymentUiState) {
        handleControls(uiState)
        when (uiState) {
            is Submitted -> with(uiState.uiModel) {
                authorizeInvoice(invoiceId, cardId)
            }
            is Failure -> binding.resultMessage.text = uiState.failure.toMessage()
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
}
