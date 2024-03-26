package com.processout.example.ui.screen.features

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import com.processout.sdk.ui.card.update.POCardUpdateConfiguration
import com.processout.sdk.ui.card.update.POCardUpdateLauncher
import com.processout.sdk.ui.googlepay.POGooglePayCardTokenizationLauncher
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.coroutines.launch

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

    private val cardsRepository = ProcessOut.instance.cards
    private lateinit var cardUpdateLauncher: POCardUpdateLauncher
    private lateinit var googlePayLauncher: POGooglePayCardTokenizationLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardUpdateLauncher = POCardUpdateLauncher.create(
            from = this,
            callback = ::handleCardUpdateResult
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
                        options = POCardUpdateConfiguration.Options(
                            cardInformation = POCardUpdateConfiguration.CardInformation(
                                maskedNumber = maskedNumber,
                                iin = card?.iin,
                                scheme = card?.scheme,
                                preferredScheme = card?.coScheme
                            ),
                            cancellation = POCancellationConfiguration(
                                secondaryAction = true,
                                backPressed = true,
                                dragDown = true,
                                touchOutside = false
                            )
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
            .onSuccess { showAlert(getString(R.string.card_update_success_format, it.id)) }
            .onFailure { showAlert(it.toMessage()) }
    }

    private fun setupGooglePay() {
        lifecycleScope.launch {
            if (!googlePayLauncher.isReadyToPay(GooglePayConfiguration.isReadyToPayRequest())) {
                return@launch
            }
            with(binding.googlePayButton) {
                val options = ButtonOptions.newBuilder()
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
            .onSuccess { showAlert(getString(R.string.google_pay_success_format, it.card.id)) }
            .onFailure { showAlert(it.toMessage()) }
    }

    private fun showAlert(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}
