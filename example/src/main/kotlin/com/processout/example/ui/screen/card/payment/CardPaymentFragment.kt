package com.processout.example.ui.screen.card.payment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.checkout.threeds.Environment
import com.processout.example.R
import com.processout.example.databinding.FragmentCardPaymentBinding
import com.processout.example.service.threeds.Checkout3DSServiceDelegate
import com.processout.example.service.threeds.POAdyen3DSService
import com.processout.example.shared.Constants
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.MainActivity
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.screen.card.payment.CardPaymentViewModelEvent.LaunchTokenization
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.checkout.threeds.POCheckout3DSService
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.CardScannerConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationLauncher
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import com.processout.sdk.ui.threeds.POTest3DSService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>(
    FragmentCardPaymentBinding::inflate
) {

    private val viewModel: CardPaymentViewModel by viewModels {
        CardPaymentViewModel.Factory()
    }

    private lateinit var launcher: POCardTokenizationLauncher
    private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = POCardTokenizationLauncher.create(
            from = this,
            delegate = DefaultCardTokenizationDelegate(
                viewModel = viewModel,
                invoices = ProcessOut.instance.invoices,
                provide3DSService = ::create3DSService
            ),
            callback = ::handle
        )
        customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                withContext(Dispatchers.Main.immediate) {
                    viewModel.events.collect { handle(it) }
                }
            }
        }
    }

    private fun handle(result: ProcessOutActivityResult<POCard>) {
        enableControls(isEnabled = true)
        result.onSuccess { card ->
            val invoiceId = viewModel.state.value.invoiceId
            showAlert(getString(R.string.authorize_invoice_success_format, invoiceId, card.id))
        }.onFailure {
            showAlert(it.toMessage())
        }
    }

    private fun handle(event: CardPaymentViewModelEvent) {
        when (event) {
            LaunchTokenization -> {
                enableControls(isEnabled = false)
                launchTokenization()
            }
        }
    }

    private fun launchTokenization() {
        launcher.launch(
            POCardTokenizationConfiguration(
                cardScanner = CardScannerConfiguration(),
                savingAllowed = true
            )
        )
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
            viewModel.submit(
                amount = amountInput.text.toString(),
                currency = currencyInput.text.toString()
            )
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
            title = getString(R.string.card_payment),
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
