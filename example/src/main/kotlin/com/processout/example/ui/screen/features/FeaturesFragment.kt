package com.processout.example.ui.screen.features

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.processout.example.R
import com.processout.example.databinding.FragmentFeaturesBinding
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest

class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(
    FragmentFeaturesBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nativeApmButton.setOnClickListener {
            findNavController().navigate(
                FeaturesFragmentDirections.actionFeaturesFragmentToAlternativePaymentMethodsFragment(
                    getString(R.string.native_apm),
                    POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
                )
            )
        }
    }
}
