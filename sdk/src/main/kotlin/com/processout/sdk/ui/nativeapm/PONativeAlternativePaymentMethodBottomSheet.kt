package com.processout.sdk.ui.nativeapm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.processout.sdk.R
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.shared.view.extensions.requestFocusAndShowKeyboard
import kotlinx.coroutines.launch

class PONativeAlternativePaymentMethodBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "PONativeAlternativePaymentMethodBottomSheet"
    }

    private var configuration: PONativeAlternativePaymentMethodConfiguration? = null

    @Suppress("DEPRECATION")
    private val viewModel: PONativeAlternativePaymentMethodViewModel by viewModels {
        PONativeAlternativePaymentMethodViewModel.Factory(
            configuration?.gatewayConfigurationId ?: String(),
            configuration?.invoiceId ?: String()
        )
    }

    private var _binding: PoBottomSheetNativeApmBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityCallback: BottomSheetCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = requireActivity() as BottomSheetCallback
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
        configuration?.run {
            if (gatewayConfigurationId.isBlank() || invoiceId.isBlank()) {
                finishWithActivityResult(
                    Activity.RESULT_CANCELED,
                    PONativeAlternativePaymentMethodResult.Failure(
                        ProcessOutException("Invalid configuration.")
                    )
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), R.style.Theme_ProcessOut_Default)
        val view = inflater.cloneInContext(contextThemeWrapper)
            .inflate(R.layout.po_bottom_sheet_native_apm, container, false)
        _binding = PoBottomSheetNativeApmBinding.bind(view)
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

    private fun handleUiState(uiState: PONativeAlternativePaymentMethodUiState) {
        when (uiState) {
            PONativeAlternativePaymentMethodUiState.Initial -> binding.root.visibility = View.INVISIBLE
            PONativeAlternativePaymentMethodUiState.Loading -> binding.root.visibility = View.INVISIBLE
            is PONativeAlternativePaymentMethodUiState.UserInput -> bindUiModel(uiState.uiModel)
            PONativeAlternativePaymentMethodUiState.Success ->
                finishWithActivityResult(
                    Activity.RESULT_OK,
                    PONativeAlternativePaymentMethodResult.Success
                )
            PONativeAlternativePaymentMethodUiState.Failure ->
                finishWithActivityResult(
                    Activity.RESULT_CANCELED,
                    PONativeAlternativePaymentMethodResult.Failure(
                        ProcessOutException("Payment failed.")
                    )
                )
        }
    }

    private fun bindUiModel(uiModel: PONativeAlternativePaymentMethodUiModel) {
        binding.root.visibility = View.VISIBLE
        binding.poTitle.text = uiModel.promptMessage
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
                if (index == 0) input.editText?.requestFocusAndShowKeyboard()
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
            finishWithActivityResult(
                Activity.RESULT_CANCELED,
                PONativeAlternativePaymentMethodResult.Canceled
            )
        }
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: PONativeAlternativePaymentMethodResult
    ) {
        requireActivity().setResult(
            resultCode,
            Intent().putExtra(
                EXTRA_RESULT,
                result
            )
        )
        finish()
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
