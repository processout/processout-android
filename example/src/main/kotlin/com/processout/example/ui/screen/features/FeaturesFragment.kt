package com.processout.example.ui.screen.features

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.processout.example.R
import com.processout.example.databinding.FragmentFeaturesBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.card.update.POCardUpdateConfiguration
import com.processout.sdk.ui.card.update.POCardUpdateLauncher
import java.util.UUID

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

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
            cardUpdateButton.setOnClickListener {
                cardUpdateLauncher.launch(
                    POCardUpdateConfiguration(
                        cardId = UUID.randomUUID().toString()
                    )
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
    }

    private fun handleCardUpdateResult(result: ProcessOutActivityResult<Nothing>) {
        when (result) {
            is ProcessOutActivityResult.Success ->
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            is ProcessOutActivityResult.Failure ->
                Toast.makeText(requireContext(), "Failure: ${result.code} ${result.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
