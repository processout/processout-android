package com.processout.sdk.ui.nativeapm

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.processout.sdk.R
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.network.exception.ProcessOutApiError
import com.processout.sdk.api.network.exception.ProcessOutApiException
import com.processout.sdk.databinding.PoBottomSheetCaptureBinding
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.view.button.POButton
import com.processout.sdk.ui.shared.view.extensions.*
import com.processout.sdk.ui.shared.view.input.InputComponent
import com.processout.sdk.ui.shared.view.input.code.CodeInput
import com.processout.sdk.ui.shared.view.input.text.TextInput
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class PONativeAlternativePaymentMethodBottomSheet : BottomSheetDialogFragment(), OnShowListener {

    companion object {
        const val TAG = "PONativeAlternativePaymentMethodBottomSheet"
        private const val MAX_INPUTS_COUNT_IN_COLLAPSED_STATE = 2
        private const val SUCCESS_FINISH_DELAY_MS = 3000L
        private const val ANIMATION_DURATION_MS = 350L
    }

    private var configuration: PONativeAlternativePaymentMethodConfiguration? = null

    private val viewModel: PONativeAlternativePaymentMethodViewModel by viewModels {
        PONativeAlternativePaymentMethodViewModel.Factory(
            requireActivity().application,
            configuration?.gatewayConfigurationId ?: String(),
            configuration?.invoiceId ?: String(),
            configuration?.options ?: PONativeAlternativePaymentMethodConfiguration.Options(),
            configuration?.uiConfiguration
        )
    }

    private var _binding: PoBottomSheetNativeApmBinding? = null
    private val binding get() = _binding!!

    private var _bindingCapture: PoBottomSheetCaptureBinding? = null
    private val bindingCapture get() = _bindingCapture!!

    private val bottomSheetDialog by lazy { requireDialog() as BottomSheetDialog }
    private val bottomSheetBehavior by lazy { bottomSheetDialog.behavior }
    private val displayHeight by lazy { resources.displayMetrics.heightPixels }
    private val maxPeekHeight by lazy { (displayHeight * 0.75).roundToInt() }
    private val minPeekHeight by lazy { resources.getDimensionPixelSize(R.dimen.po_bottomSheet_minHeight) }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private lateinit var activityCallback: BottomSheetCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = requireActivity() as BottomSheetCallback
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
        configuration?.run {
            if (gatewayConfigurationId.isBlank() || invoiceId.isBlank()) {
                finishWithActivityResult(
                    PONativeAlternativePaymentMethodResult.Failure("Invalid configuration.")
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
        configuration?.style?.run {
            binding.applyStyle(this)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clipRootToOutline()
        dispatchBackPressed()

        with(bottomSheetDialog) {
            isCancelable = viewModel.options.isBottomSheetCancelable
            setOnShowListener(this@PONativeAlternativePaymentMethodBottomSheet)
        }

        binding.poSubmitButton.setOnClickListener { onSubmitClick() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { handleUiState(it) }
            }
        }
    }

    private fun clipRootToOutline() {
        binding.root.outlineProvider = TopRoundedCornersOutlineProvider(
            R.dimen.po_bottomSheet_cornerRadius
        )
        binding.root.clipToOutline = true
    }

    override fun onShow(dialog: DialogInterface) {
        allowExpandToFullScreen()
        adjustPeekHeight(animate = false)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.poContainer.updateLayoutParams {
                        height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    with(binding.poContainer) {
                        updateLayoutParams {
                            val containerCoordinateY = IntArray(2)
                                .also { getLocationOnScreen(it) }.let { it[1] }

                            val windowInsets = ViewCompat.getRootWindowInsets(bottomSheet)
                            val systemBarsHeight = windowInsets?.getInsetsIgnoringVisibility(
                                WindowInsetsCompat.Type.systemBars()
                            )?.bottom ?: 0

                            var keyboardHeight = windowInsets?.getInsets(
                                WindowInsetsCompat.Type.ime()
                            )?.bottom ?: 0
                            if (keyboardHeight != 0) {
                                keyboardHeight -= systemBarsHeight
                            }

                            var updatedHeight = displayHeight - keyboardHeight - containerCoordinateY
                            if (updatedHeight < bottomSheetBehavior.peekHeight) {
                                updatedHeight = bottomSheetBehavior.peekHeight
                            }
                            height = updatedHeight
                        }
                    }
                }
            }
        })
    }

    private fun allowExpandToFullScreen() {
        val bottomSheet: FrameLayout = requireDialog().findViewById(
            com.google.android.material.R.id.design_bottom_sheet
        )
        bottomSheet.updateLayoutParams {
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun adjustBottomSheetState(previousInputsCount: Int, currentInputsCount: Int) {
        if (currentInputsCount != previousInputsCount &&
            currentInputsCount > MAX_INPUTS_COUNT_IN_COLLAPSED_STATE
        ) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun adjustPeekHeight(animate: Boolean) {
        val peekHeight = when (viewModel.uiState.value) {
            PONativeAlternativePaymentMethodUiState.Loading -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.UserInput -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.Capture -> maxPeekHeight
            is PONativeAlternativePaymentMethodUiState.Success -> maxPeekHeight
            is PONativeAlternativePaymentMethodUiState.Failure -> bottomSheetBehavior.peekHeight
        }

        if (peekHeight != bottomSheetBehavior.peekHeight) {
            if (animate)
                animatePeekHeight(peekHeight)
            else bottomSheetBehavior.peekHeight = peekHeight
            binding.poContainer.updateLayoutParams {
                height = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    ViewGroup.LayoutParams.MATCH_PARENT else peekHeight
            }
        }
    }

    private fun animatePeekHeight(updatedHeight: Int) {
        ValueAnimator.ofInt(bottomSheetBehavior.peekHeight, updatedHeight).apply {
            addUpdateListener {
                bottomSheetBehavior.peekHeight = it.animatedValue as Int
            }
            duration = ANIMATION_DURATION_MS
            start()
        }
    }

    private fun handleUiState(uiState: PONativeAlternativePaymentMethodUiState) {
        when (uiState) {
            PONativeAlternativePaymentMethodUiState.Loading ->
                bindLoading()
            is PONativeAlternativePaymentMethodUiState.UserInput ->
                bindUserInput(uiState.uiModel)
            is PONativeAlternativePaymentMethodUiState.Capture ->
                showCapture(uiState.uiModel)
            is PONativeAlternativePaymentMethodUiState.Success ->
                handleSuccess(uiState.uiModel)
            is PONativeAlternativePaymentMethodUiState.Failure ->
                handleFailure(uiState.message, uiState.cause)
        }
    }

    private fun bindLoading() {
        binding.poLoading.root.visibility = View.VISIBLE
        binding.poScrollableContent.visibility = View.GONE
        binding.poSubmitButton.visibility = View.GONE
    }

    private fun bindUserInput(uiModel: PONativeAlternativePaymentMethodUiModel) {
        if (viewModel.animateViewTransition) {
            viewModel.animateViewTransition = false
            crossfade(
                viewsToHide = listOf(binding.poLoading.root),
                viewsToShow = listOf(binding.poScrollableContent, binding.poSubmitButton),
                duration = ANIMATION_DURATION_MS
            )
        } else {
            binding.poLoading.root.visibility = View.GONE
            binding.poScrollableContent.visibility = View.VISIBLE
            binding.poSubmitButton.visibility = View.VISIBLE
        }

        binding.poTitle.text = uiModel.title
        bindSubmitButton(uiModel)
        bindInputs(uiModel.inputParameters)
    }

    private fun bindSubmitButton(uiModel: PONativeAlternativePaymentMethodUiModel) {
        with(binding.poSubmitButton) {
            text = uiModel.submitButtonText
            if (uiModel.isSubmitting) {
                setState(POButton.State.PROGRESS)
            } else if (uiModel.isSubmitAllowed) {
                setState(POButton.State.ENABLED)
            } else {
                setState(POButton.State.DISABLED)
            }
        }
    }

    private fun bindInputs(inputParameters: List<InputParameter>) {
        val inputsCountBefore = binding.poInputsContainer.childCount

        // find and remove inputs that currently present in the container
        // but does not exist in provided input parameters
        val currentInputIds = mutableListOf<Int>()
        binding.poInputsContainer.children.forEach {
            currentInputIds.add(it.id)
        }
        currentInputIds.removeAll(inputParameters.map { it.id })
        currentInputIds.forEach {
            with(binding.poInputsContainer) {
                removeView(findViewById(it))
            }
        }

        inputParameters.forEachIndexed { index, parameter ->
            // only add inputs that has not been added yet
            binding.poInputsContainer.findViewById(parameter.id) ?: run {
                val input = addInputComponent(parameter)
                if (index == 0) {
                    input.requestFocusAndShowKeyboard()
                }
            }
        }

        val inputsCountAfter = binding.poInputsContainer.childCount
        adjustBottomSheetState(inputsCountBefore, inputsCountAfter)
    }

    private fun addInputComponent(inputParameter: InputParameter): InputComponent {
        val length = inputParameter.parameter.length
        val input = if (inputParameter.parameter.type == ParameterType.numeric &&
            length != null && length in CodeInput.LENGTH_MIN..CodeInput.LENGTH_MAX
        ) {
            CodeInput(
                requireContext(),
                inputParameter = inputParameter,
                style = configuration?.style?.codeInput
            )
        } else {
            TextInput(
                requireContext(),
                inputParameter = inputParameter,
                style = configuration?.style?.input
            )
        }

        input.doAfterValueChanged { value ->
            viewModel.updateInputValue(inputParameter.id, value)
        }
        input.onKeyboardSubmitClick {
            viewModel.uiState.value.doWhenUserInput { uiModel ->
                if (uiModel.isSubmitAllowed) {
                    onSubmitClick()
                }
            }
        }
        binding.poInputsContainer.addView(input)
        return input
    }

    private fun onSubmitClick() {
        binding.poSubmitButton.isClickable = false

        val data = mutableMapOf<String, String>()
        binding.poInputsContainer.children.forEach { view ->
            (view as InputComponent).let {
                it.inputParameter?.parameter?.run {
                    data[key] = it.value
                }
            }
        }
        viewModel.submitPayment(data)
    }

    private fun initCaptureView() {
        view?.hideKeyboard()
        if (_bindingCapture == null) {
            with(binding.poContainer) {
                removeAllViews()
                layoutInflater.inflate(
                    R.layout.po_bottom_sheet_capture,
                    binding.poContainer, true
                ).also {
                    _bindingCapture = PoBottomSheetCaptureBinding.bind(it)
                    configuration?.style?.run {
                        bindingCapture.applyStyle(this)
                    }
                }
            }
        }
    }

    private fun showCapture(uiModel: PONativeAlternativePaymentMethodUiModel) {
        with(binding.poContainer) {
            if (viewModel.animateViewTransition) {
                viewModel.animateViewTransition = false
                animate()
                    .alpha(0f)
                    .setDuration(ANIMATION_DURATION_MS)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            bindCapture(uiModel)
                            adjustPeekHeight(animate = true)
                            fadeIn(listOf(this@with), ANIMATION_DURATION_MS)
                        }
                    })
            } else {
                bindCapture(uiModel)
            }
        }
    }

    private fun bindCapture(uiModel: PONativeAlternativePaymentMethodUiModel) {
        initCaptureView()
        bindingCapture.poMessage.text = uiModel.customerActionMessage
        bindingCapture.poLogo.load(uiModel.logoUrl)
        bindingCapture.poActionImage.load(uiModel.customerActionImageUrl)
        bindingCapture.poActionImage.visibility = View.VISIBLE
        bindingCapture.poSuccessImage.visibility = View.GONE
    }

    private fun handleSuccess(uiModel: PONativeAlternativePaymentMethodUiModel) {
        setActivityResult(
            Activity.RESULT_OK,
            PONativeAlternativePaymentMethodResult.Success
        )
        if (viewModel.options.waitsPaymentConfirmation) {
            handler.postDelayed({ finish() }, SUCCESS_FINISH_DELAY_MS)
            showSuccess(uiModel)
        } else {
            finish()
        }
    }

    private fun showSuccess(uiModel: PONativeAlternativePaymentMethodUiModel) {
        initCaptureView()
        if (viewModel.animateViewTransition) {
            viewModel.animateViewTransition = false
            fadeOut(
                listOf(
                    bindingCapture.poMessage,
                    bindingCapture.poActionImage
                ),
                ANIMATION_DURATION_MS
            )

            bindingCapture.poBackgroundDecoration.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION_MS)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        bindSuccess(uiModel)
                        adjustPeekHeight(animate = true)
                        fadeIn(
                            listOf(
                                bindingCapture.poBackgroundDecoration,
                                bindingCapture.poMessage,
                                bindingCapture.poSuccessImage
                            ),
                            ANIMATION_DURATION_MS
                        )
                    }
                })
        } else {
            bindSuccess(uiModel)
        }
    }

    private fun bindSuccess(uiModel: PONativeAlternativePaymentMethodUiModel) {
        configuration?.style?.backgroundDecoration?.let {
            bindingCapture.poBackgroundDecoration.applyStyle(it.success)
        } ?: bindingCapture.poBackgroundDecoration.setBackgroundDecoration(
            innerColor = ContextCompat.getColor(requireContext(), R.color.poBackgroundSuccessDark),
            outerColor = ContextCompat.getColor(requireContext(), R.color.poBackgroundSuccessLight)
        )

        configuration?.style?.successMessage?.let {
            bindingCapture.poMessage.applyStyle(it)
        } ?: bindingCapture.poMessage.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.poTextSuccess)
        )

        bindingCapture.poMessage.text = uiModel.successMessage
        bindingCapture.poLogo.load(uiModel.logoUrl)
        bindingCapture.poActionImage.visibility = View.GONE
        bindingCapture.poSuccessImage.visibility = View.VISIBLE
    }

    private fun handleFailure(message: String, cause: Exception?) {
        var code: Int? = null
        var apiError: ProcessOutApiError? = null
        cause?.let {
            if (it is ProcessOutApiException) {
                code = it.code
                apiError = it.apiError
            }
        }

        finishWithActivityResult(
            PONativeAlternativePaymentMethodResult.Failure(
                message, cause?.message, code, apiError
            )
        )
    }

    override fun onPause() {
        super.onPause()
        clearAnimationListeners()
        handler.removeCallbacksAndMessages(null)
        if (viewModel.uiState.value is PONativeAlternativePaymentMethodUiState.Success) {
            finish()
        }
    }

    private fun clearAnimationListeners() {
        if (_binding != null) {
            binding.poContainer.animate().setListener(null)
        }
        if (_bindingCapture != null) {
            bindingCapture.poBackgroundDecoration.animate().setListener(null)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        finishWithActivityResult(
            PONativeAlternativePaymentMethodResult.Canceled
        )
    }

    private fun dispatchBackPressed() {
        bottomSheetDialog.onBackPressedDispatcher.addCallback(this) {
            finishWithActivityResult(
                PONativeAlternativePaymentMethodResult.Canceled
            )
        }
    }

    private fun finishWithActivityResult(
        result: PONativeAlternativePaymentMethodResult
    ) {
        // prevent overriding of Success result
        if (viewModel.uiState.value !is PONativeAlternativePaymentMethodUiState.Success) {
            setActivityResult(Activity.RESULT_CANCELED, result)
        }
        finish()
    }

    private fun setActivityResult(
        resultCode: Int,
        result: PONativeAlternativePaymentMethodResult
    ) {
        if (isAdded) {
            requireActivity().setResult(
                resultCode,
                Intent().putExtra(
                    EXTRA_RESULT,
                    result
                )
            )
        }
    }

    private fun finish() {
        if (isAdded) {
            dismiss()
            activityCallback.onBottomSheetFinished()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bindingCapture = null
    }
}
