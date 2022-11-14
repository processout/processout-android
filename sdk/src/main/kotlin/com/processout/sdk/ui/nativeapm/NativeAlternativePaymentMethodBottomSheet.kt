package com.processout.sdk.ui.nativeapm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.processout.sdk.R
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import kotlinx.coroutines.launch

internal class NativeAlternativePaymentMethodBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "NativeAlternativePaymentMethodBottomSheet"
    }

    private val viewModel: NativeAlternativePaymentMethodViewModel by viewModels {
        NativeAlternativePaymentMethodViewModel.Factory(
            arguments?.getString(NativeAPMBundleKey.GATEWAY_CONFIGURATION_ID)!!,
            arguments?.getString(NativeAPMBundleKey.INVOICE_ID)!!
        )
    }

    private var _binding: PoBottomSheetNativeApmBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityCallback: BottomSheetCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = requireActivity() as BottomSheetCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoBottomSheetNativeApmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dispatchBackPressed()

        binding.poSubmitButton.setOnClickListener { onSubmitClick() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { handleUiState(it) }
            }
        }
    }

    private fun handleUiState(uiState: NativeAPMUiState) {
        when (uiState) {
            NativeAPMUiState.Initial -> binding.root.visibility = View.INVISIBLE
            NativeAPMUiState.Loading -> binding.root.visibility = View.INVISIBLE
            is NativeAPMUiState.UserInput -> bindUiModel(uiState.uiModel)
            NativeAPMUiState.Success -> {
                // TODO: callback Success to client app
                finish()
            }
            NativeAPMUiState.Failure -> {
                // TODO: callback Failure to client app
                finish()
            }
        }
    }

    private fun bindUiModel(uiModel: NativeAPMUiModel) {
        binding.root.visibility = View.VISIBLE
        binding.poLogo.load(uiModel.logoUrl)
        binding.poSubmitButton.isEnabled = uiModel.isSubmitAllowed
        bindInputs(uiModel.inputParameters)
    }

    private fun bindInputs(inputParameters: List<InputParameter>) {
        // find and remove inputs that currently present in the container
        // but does not exist in provided input parameters
        val currentInputIds = mutableListOf<Int>()
        binding.poInputsContainer.children.forEach {
            currentInputIds.add(it.id)
        }
        currentInputIds.removeAll(inputParameters.map { it.id })
        currentInputIds.forEach {
            with(binding.poInputsContainer) {
                removeView(findViewById<ViewGroup>(it))
            }
        }

        inputParameters.forEachIndexed { index, parameter ->
            // only add inputs that has not been added yet
            binding.poInputsContainer.findViewById<ViewGroup>(parameter.id) ?: run {
                val input = layoutInflater.inflate(
                    R.layout.po_text_input, binding.poInputsContainer, false
                ) as TextInputLayout

                input.id = parameter.id
                input.editText?.apply {
                    tag = parameter
                    hint = parameter.hint
                    inputType = parameter.toInputType()
                    setText(parameter.value, TextView.BufferType.EDITABLE)
                    doAfterTextChanged {
                        viewModel.updateInputValue(parameter.id, text.toString())
                    }
                }
                binding.poInputsContainer.addView(input)
                if (index == 0) input.editText?.requestFocus()
            }
        }
    }

    private fun onSubmitClick() {
        val data = mutableMapOf<String, String>()
        binding.poInputsContainer.children.forEach { view ->
            (view as TextInputLayout).let {
                val key = (it.editText?.tag as InputParameter).parameter.key
                val value = it.editText?.text.toString()
                data[key] = value
            }
        }
        viewModel.submitPayment(data)
    }

    private fun dispatchBackPressed() {
        (requireDialog() as BottomSheetDialog).onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    private fun finish() {
        dismiss()
        activityCallback.onBottomSheetBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
