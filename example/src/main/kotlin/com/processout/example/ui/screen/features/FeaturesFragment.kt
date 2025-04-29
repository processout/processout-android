package com.processout.example.ui.screen.features

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.processout.example.R
import com.processout.example.databinding.FragmentFeaturesBinding
import com.processout.example.service.googlepay.GooglePayConfiguration
import com.processout.example.service.googlepay.GooglePayConstants
import com.processout.example.shared.toMessage
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POGooglePayCardTokenizationData
import com.processout.sdk.core.*
import com.processout.sdk.ui.card.scanner.POCardScannerConfiguration
import com.processout.sdk.ui.card.scanner.POCardScannerLauncher
import com.processout.sdk.ui.card.scanner.recognition.POScannedCard
import com.processout.sdk.ui.card.update.POCardUpdateConfiguration
import com.processout.sdk.ui.card.update.POCardUpdateConfiguration.CardInformation
import com.processout.sdk.ui.card.update.POCardUpdateDelegate
import com.processout.sdk.ui.card.update.POCardUpdateLauncher
import com.processout.sdk.ui.googlepay.POGooglePayCardTokenizationLauncher
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog
import kotlinx.coroutines.launch

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

    private val cardsRepository = ProcessOut.instance.cards

    private lateinit var cardUpdateLauncher: POCardUpdateLauncher
    private lateinit var cardScannerLauncher: POCardScannerLauncher
    private lateinit var googlePayLauncher: POGooglePayCardTokenizationLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardUpdateLauncher = POCardUpdateLauncher.create(
            from = this,
            delegate = object : POCardUpdateDelegate {},
            callback = ::handleCardUpdateResult
        )
        cardScannerLauncher = POCardScannerLauncher.create(
            from = this,
            callback = ::handleCardScannerResult
        )
        googlePayLauncher = POGooglePayCardTokenizationLauncher.create(
            from = this,
            walletOptions = WalletOptions.Builder()
                .setEnvironment(GooglePayConstants.PAYMENTS_ENVIRONMENT)
                .build(),
            callback = ::handleGooglePayResult
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        with(binding) {
            dynamicCheckoutButton.setOnClickListener {
                navController.navigate(
                    FeaturesFragmentDirections.actionFeaturesFragmentToDynamicCheckoutFragment()
                )
            }
            nativeApmButton.setOnClickListener {
                navController.navigate(
                    FeaturesFragmentDirections.actionFeaturesFragmentToAlternativePaymentMethodsFragment(
                        getString(R.string.native_apm),
                        POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
                    )
                )
            }
            cardPaymentButton.setOnClickListener {
                navController.navigate(
                    FeaturesFragmentDirections.actionFeaturesFragmentToCardPaymentFragment()
                )
            }
        }
        setupCardUpdate()
        setupCardScanner()
        setupGooglePay()
    }

    private fun setupCardUpdate() {
        binding.cardUpdateButton.setOnClickListener {
            lifecycleScope.launch {
                val number = "5137210000000158"
                val maskedNumber = number.replaceRange(
                    startIndex = 4,
                    endIndex = 14,
                    replacement = " **** **** **"
                )
                val card = tokenizeCard(number)
                cardUpdateLauncher.launch(
                    POCardUpdateConfiguration(
                        cardId = card?.id ?: String(),
                        cardInformation = CardInformation(
                            maskedNumber = maskedNumber,
                            iin = card?.iin,
                            scheme = card?.scheme,
                            preferredScheme = card?.coScheme
                        ),
                        bottomSheet = POBottomSheetConfiguration(
                            height = WrapContent,
                            expandable = false
                        )
                    )
                )
            }
        }
    }

    private suspend fun tokenizeCard(number: String): POCard? {
        val request = POCardTokenizationRequest(
            number = number,
            expMonth = 10,
            expYear = 2030,
            cvc = "123"
        )
        return cardsRepository.tokenize(request).getOrNull()
    }

    private fun handleCardUpdateResult(result: ProcessOutActivityResult<POCard>) {
        result
            .onSuccess {
                showAlert(
                    title = getString(R.string.card_update),
                    message = getString(R.string.card_update_success_format, it.id)
                )
            }
            .onFailure {
                showAlert(
                    title = getString(R.string.card_update),
                    message = it.toMessage()
                )
            }
    }

    private fun setupCardScanner() {
        binding.cardScannerButton.setOnClickListener {
            cardScannerLauncher.launch(POCardScannerConfiguration())
        }
    }

    private fun handleCardScannerResult(result: ProcessOutActivityResult<POScannedCard>) {
        result
            .onSuccess {
                showAlert(
                    title = getString(R.string.card_scanner),
                    message = it.toString()
                )
            }
            .onFailure {
                showAlert(
                    title = getString(R.string.card_scanner),
                    message = it.toMessage()
                )
            }
    }

    private fun setupGooglePay() {
        lifecycleScope.launch {
            if (!googlePayLauncher.isReadyToPay(GooglePayConfiguration.isReadyToPayRequest())) {
                return@launch
            }
            with(binding.googlePayButton) {
                val options = ButtonOptions.newBuilder()
                    .setButtonType(ButtonConstants.ButtonType.PLAIN)
                    .setAllowedPaymentMethods(GooglePayConfiguration.allowedPaymentMethods.toString())
                    .build()
                initialize(options)
                setOnClickListener { launchGooglePay() }
            }
        }
    }

    private fun launchGooglePay() {
        val paymentDataRequestJson = GooglePayConfiguration.getPaymentDataRequest(priceCents = 100L)
        googlePayLauncher.launch(paymentDataRequestJson)
    }

    private fun handleGooglePayResult(result: ProcessOutResult<POGooglePayCardTokenizationData>) {
        result
            .onSuccess {
                showAlert(
                    title = getString(R.string.google_pay),
                    message = getString(R.string.google_pay_success_format, it.card.id)
                )
            }
            .onFailure {
                showAlert(
                    title = getString(R.string.google_pay),
                    message = it.toMessage()
                )
            }
    }

    private fun showAlert(title: String, message: String) {
        POAlertDialog(
            context = requireContext(),
            title = title,
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
