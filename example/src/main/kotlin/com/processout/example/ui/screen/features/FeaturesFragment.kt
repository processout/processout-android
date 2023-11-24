package com.processout.example.ui.screen.features

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.processout.example.R
import com.processout.example.databinding.FragmentFeaturesBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.ui.card.update.POCardUpdateConfiguration
import com.processout.sdk.ui.card.update.POCardUpdateLauncher
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.coroutines.launch

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

    private val cardsRepository = ProcessOut.instance.cards
    private lateinit var cardUpdateLauncher: POCardUpdateLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardUpdateLauncher = POCardUpdateLauncher.create(
            from = this,
            callback = ::handleCardUpdateResult
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        with(binding) {
            cardPaymentButton.setOnClickListener {
                navController.navigate(
                    FeaturesFragmentDirections.actionFeaturesFragmentToCardPaymentFragment()
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
        }
        setupCardUpdate()
    }

    private fun setupCardUpdate() {
        binding.cardUpdateButton.setOnClickListener {
            lifecycleScope.launch {
                val number = "5137210000000158"
                val maskedNumber = number.replaceRange(
                    startIndex = 4,
                    endIndex = 14,
                    replacement = "**********"
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

    private fun handleCardUpdateResult(result: ProcessOutActivityResult<POUnit>) {
        when (result) {
            is ProcessOutActivityResult.Success ->
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            is ProcessOutActivityResult.Failure ->
                Toast.makeText(requireContext(), "Failure: ${result.code} ${result.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
