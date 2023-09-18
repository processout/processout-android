package com.processout.example.ui.screen.apm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.processout.example.databinding.FragmentAlternativePaymentMethodsBinding
import com.processout.example.databinding.ItemApmBinding
import com.processout.example.ui.screen.apm.AlternativePaymentMethodsUiState.Initial
import com.processout.example.ui.screen.apm.AlternativePaymentMethodsUiState.Started
import com.processout.example.ui.screen.base.BaseFragment
import com.processout.example.ui.shared.setup
import kotlinx.coroutines.launch

class AlternativePaymentMethodsFragment : BaseFragment<FragmentAlternativePaymentMethodsBinding>(
    FragmentAlternativePaymentMethodsBinding::inflate
) {

    private val args: AlternativePaymentMethodsFragmentArgs by navArgs()

    private val viewModel: AlternativePaymentMethodsViewModel by viewModels {
        AlternativePaymentMethodsViewModel.Factory(args.filter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { handle(it) }
            }
        }
    }

    private fun handle(uiState: AlternativePaymentMethodsUiState) {
        when (uiState) {
            Initial -> binding.circularProgressIndicator.visibility = View.VISIBLE
            is Started -> {
                binding.circularProgressIndicator.visibility = View.GONE
                bind(uiState.uiModel)
            }
        }
    }

    private fun bind(uiModel: AlternativePaymentMethodsUiModel) {
        binding.recyclerView.setup(
            uiModel.gatewayConfigurations,
            ItemApmBinding::inflate,
            { holder, data ->
                holder?.item?.text = data.name
                holder?.item?.setOnClickListener {
                    findNavController().navigate(
                        AlternativePaymentMethodsFragmentDirections
                            .actionAlternativePaymentMethodsFragmentToPaymentFragment(
                                data.name, data.id
                            )
                    )
                }
            }
        )
    }
}
