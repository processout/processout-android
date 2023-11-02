package com.processout.example.ui.screen.features

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.processout.example.R
import com.processout.example.databinding.FragmentFeaturesBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.ui.card.update.POCardUpdateActivity

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

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
                Intent(requireActivity(), POCardUpdateActivity::class.java).also {
                    startActivity(it)
                }
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
}
