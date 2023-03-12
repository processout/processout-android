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
import androidx.core.animation.addListener
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
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.databinding.PoBottomSheetCaptureBinding
import com.processout.sdk.databinding.PoBottomSheetNativeApmBinding
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.view.button.POButton
import com.processout.sdk.ui.shared.view.extensions.*
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent
import com.processout.sdk.ui.shared.view.input.code.CodeInput
import com.processout.sdk.ui.shared.view.input.text.TextInput
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class PONativeAlternativePaymentMethodBottomSheet : BottomSheetDialogFragment(), OnShowListener {

    companion object {
        const val TAG = "PONativeAlternativePaymentMethodBottomSheet"
        private const val REQUIRED_DISPLAY_HEIGHT_PERCENTAGE = 0.62
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
            configuration?.options ?: PONativeAlternativePaymentMethodConfiguration.Options()
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
                    PONativeAlternativePaymentMethodResult.Failure(
                        "Invalid configuration.",
                        POFailure.Code.Internal()
                    ), dispatchEvent = true
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
            with(viewModel.options.cancellation) {
                isCancelable = dragDown
                setCanceledOnTouchOutside(touchOutside)
            }
            setOnShowListener(this@PONativeAlternativePaymentMethodBottomSheet)
        }

        binding.poPrimaryButton.setOnClickListener { onSubmitClick() }
        binding.poSecondaryButton.setOnClickListener { onCancelClick() }

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

    private fun adjustPeekHeight(animate: Boolean) {
        val peekHeight = when (viewModel.uiState.value) {
            PONativeAlternativePaymentMethodUiState.Loading -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.Loaded -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.UserInput -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.Submitted -> minPeekHeight
            is PONativeAlternativePaymentMethodUiState.Capture -> maxPeekHeight
            is PONativeAlternativePaymentMethodUiState.Success -> maxPeekHeight
            is PONativeAlternativePaymentMethodUiState.Failure -> bottomSheetBehavior.peekHeight
        }

        if (peekHeight != bottomSheetBehavior.peekHeight) {
            if (animate) {
                animatePeekHeight(peekHeight)
            } else {
                bottomSheetBehavior.peekHeight = peekHeight
                setDefaultBottomSheetState()
            }
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
            addListener(onEnd = {
                setDefaultBottomSheetState()
            })
            duration = ANIMATION_DURATION_MS
            start()
        }
    }

    private fun setDefaultBottomSheetState() {
        bottomSheetBehavior.skipCollapsed = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isDraggable = true
    }

    private fun adjustBottomSheetState(previousInputsCount: Int, currentInputsCount: Int) {
        if (currentInputsCount != previousInputsCount) {
            val forceExpand = displayHeight * REQUIRED_DISPLAY_HEIGHT_PERCENTAGE < minPeekHeight
            if (forceExpand) {
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.isDraggable = viewModel.options.cancellation.dragDown
            } else if (shouldExpandAllowingCollapse(currentInputsCount)) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun shouldExpandAllowingCollapse(inputsCount: Int) =
        bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED &&
                inputsCount > MAX_INPUTS_COUNT_IN_COLLAPSED_STATE ||
                (inputsCount >= MAX_INPUTS_COUNT_IN_COLLAPSED_STATE &&
                        viewModel.options.secondaryAction != null)

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
                handleFailure(uiState.failure)
            else -> {}
        }
    }

    private fun bindLoading() {
        binding.poLoading.root.visibility = View.VISIBLE
        binding.poScrollableContent.visibility = View.GONE
        binding.poPrimaryButton.visibility = View.GONE
        binding.poSecondaryButton.visibility = View.GONE
    }

    private fun bindUserInput(uiModel: PONativeAlternativePaymentMethodUiModel) {
        binding.poTitle.text = uiModel.title
        bindPrimaryButton(uiModel)
        bindSecondaryButton(uiModel)

        if (viewModel.animateViewTransition) {
            viewModel.animateViewTransition = false
            crossfade(
                viewsToHide = listOf(binding.poLoading.root),
                viewsToShow = mutableListOf(
                    binding.poScrollableContent,
                    binding.poPrimaryButton
                ).also { list ->
                    viewModel.options.secondaryAction?.let {
                        list.add(binding.poSecondaryButton)
                    }
                },
                duration = ANIMATION_DURATION_MS
            )
        } else {
            binding.poLoading.root.visibility = View.GONE
            binding.poScrollableContent.visibility = View.VISIBLE
            binding.poPrimaryButton.visibility = View.VISIBLE
            viewModel.options.secondaryAction?.let {
                binding.poSecondaryButton.visibility = View.VISIBLE
            }
        }

        bindInputs(uiModel)
    }

    private fun bindPrimaryButton(uiModel: PONativeAlternativePaymentMethodUiModel) {
        with(binding.poPrimaryButton) {
            text = uiModel.primaryActionText
            if (uiModel.isSubmitting) {
                setState(POButton.State.PROGRESS)
            } else if (uiModel.isSubmitAllowed()) {
                setState(POButton.State.ENABLED)
            } else {
                setState(POButton.State.DISABLED)
            }
        }
    }

    private fun bindSecondaryButton(uiModel: PONativeAlternativePaymentMethodUiModel) {
        viewModel.options.secondaryAction?.let {
            with(binding.poSecondaryButton) {
                text = uiModel.secondaryActionText
                if (uiModel.isSubmitting) {
                    setState(POButton.State.DISABLED)
                } else {
                    setState(POButton.State.ENABLED)
                }
            }
        }
    }

    private fun bindInputs(uiModel: PONativeAlternativePaymentMethodUiModel) {
        val inputParameters = resolveInputParametersState(uiModel)
        val inputsCountBefore = binding.poInputsContainer.childCount

        // find and remove inputs that currently present in the container
        // but does not exist in provided input parameters
        val currentInputIds = mutableListOf<Int>()
        binding.poInputsContainer.children.forEach {
            currentInputIds.add(it.id)
        }
        currentInputIds.removeAll(inputParameters.map { it.viewId })
        currentInputIds.forEach {
            with(binding.poInputsContainer) {
                removeView(findViewById(it))
            }
        }

        inputParameters.forEach { inputParameter ->
            with(findOrAddInputComponent(inputParameter)) {
                setState(inputParameter.state)
            }
        }

        val inputsCountAfter = binding.poInputsContainer.childCount
        adjustBottomSheetState(inputsCountBefore, inputsCountAfter)
        resolveInputFocus(uiModel.focusedInputId)
    }

    private fun resolveInputParametersState(uiModel: PONativeAlternativePaymentMethodUiModel) =
        uiModel.inputParameters.run {
            if (uiModel.isSubmitting)
                map { it.copy(state = Input.State.Default(editable = false)) }
            else this@run
        }

    private fun findOrAddInputComponent(inputParameter: InputParameter): InputComponent =
        binding.poInputsContainer.findViewById<View>(inputParameter.viewId)?.let {
            (it as InputComponent)
        } ?: addInputComponent(inputParameter)

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
            viewModel.updateInputValue(inputParameter.parameter.key, value)
        }
        input.onFocused { id ->
            viewModel.updateFocusedInputId(id)
        }
        input.onKeyboardSubmitClick {
            viewModel.uiState.value.doWhenUserInput { uiModel ->
                if (uiModel.isSubmitAllowed()) {
                    onSubmitClick()
                }
            }
        }
        binding.poInputsContainer.addView(input)
        return input
    }

    private fun resolveInputFocus(focusedInputId: Int) {
        binding.poInputsContainer.children.forEachIndexed { index, view ->
            with(view as InputComponent) {
                if ((focusedInputId == View.NO_ID && index == 0) ||
                    view.id == focusedInputId
                ) requestFocusAndShowKeyboard()
            }
        }
    }

    private fun onSubmitClick() {
        binding.poPrimaryButton.isClickable = false

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

    private fun onCancelClick() {
        binding.poSecondaryButton.isClickable = false
        finishWithActivityResult(
            PONativeAlternativePaymentMethodResult.Failure(
                "Cancelled by user with secondary cancel action.",
                POFailure.Code.Cancelled
            ), dispatchEvent = true
        )
    }

    private fun initCaptureView() {
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
        if (uiModel.showCustomerAction()) {
            bindingCapture.poCircularProgressIndicator.visibility = View.GONE
            bindingCapture.poMessage.text = uiModel.customerActionMessage
            bindingCapture.poMessage.visibility = View.VISIBLE
            bindingCapture.poLogo.load(uiModel.logoUrl)
            bindingCapture.poLogo.visibility = View.VISIBLE
            bindingCapture.poActionImage.load(uiModel.customerActionImageUrl)
            bindingCapture.poActionImage.visibility = View.VISIBLE
        } else {
            bindingCapture.poCircularProgressIndicator.visibility = View.VISIBLE
            bindingCapture.poMessage.visibility = View.GONE
            bindingCapture.poLogo.visibility = View.GONE
            bindingCapture.poActionImage.visibility = View.GONE
        }
        bindingCapture.poSuccessImage.visibility = View.GONE
    }

    private fun handleSuccess(uiModel: PONativeAlternativePaymentMethodUiModel) {
        if (viewModel.options.waitsPaymentConfirmation &&
            viewModel.options.skipSuccessScreen.not()
        ) {
            handler.postDelayed({ finishWithSuccess() }, SUCCESS_FINISH_DELAY_MS)
            showSuccess(uiModel)
        } else {
            finishWithSuccess()
        }
    }

    private fun showSuccess(uiModel: PONativeAlternativePaymentMethodUiModel) {
        initCaptureView()
        if (viewModel.animateViewTransition) {
            viewModel.animateViewTransition = false
            fadeOut(
                listOf(
                    bindingCapture.poCircularProgressIndicator,
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
                            mutableListOf(
                                bindingCapture.poBackgroundDecoration,
                                bindingCapture.poMessage,
                                bindingCapture.poSuccessImage
                            ).also {
                                if (uiModel.showCustomerAction().not()) {
                                    it.add(bindingCapture.poLogo)
                                }
                            },
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

        bindingCapture.poCircularProgressIndicator.visibility = View.GONE
        bindingCapture.poMessage.text = uiModel.successMessage
        bindingCapture.poMessage.visibility = View.VISIBLE
        bindingCapture.poLogo.load(uiModel.logoUrl)
        bindingCapture.poLogo.visibility = View.VISIBLE
        bindingCapture.poActionImage.visibility = View.GONE
        bindingCapture.poSuccessImage.visibility = View.VISIBLE
    }

    private fun handleFailure(failure: ProcessOutResult.Failure) {
        with(failure) {
            finishWithActivityResult(
                PONativeAlternativePaymentMethodResult.Failure(
                    message, code, invalidFields
                ), dispatchEvent = false
            )
        }
    }

    override fun onPause() {
        super.onPause()
        clearAnimationListeners()
        handler.removeCallbacksAndMessages(null)
        finishWithSuccess()
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
            PONativeAlternativePaymentMethodResult.Failure(
                "Cancelled by user with swipe or outside touch.",
                POFailure.Code.Cancelled
            ), dispatchEvent = true
        )
    }

    private fun dispatchBackPressed() {
        bottomSheetDialog.onBackPressedDispatcher.addCallback(this) {
            finishWithActivityResult(
                PONativeAlternativePaymentMethodResult.Failure(
                    "Cancelled by user with back press or gesture.",
                    POFailure.Code.Cancelled
                ), dispatchEvent = true
            )
        }
    }

    private fun finishWithActivityResult(
        failure: PONativeAlternativePaymentMethodResult.Failure,
        dispatchEvent: Boolean
    ) {
        if (finishWithSuccess().not()) {
            if (dispatchEvent) {
                viewModel.onViewFailure(failure)
            }
            setActivityResult(Activity.RESULT_CANCELED, failure)
            finish()
        }
    }

    private fun finishWithSuccess(): Boolean {
        if (viewModel.uiState.value is PONativeAlternativePaymentMethodUiState.Success) {
            setActivityResult(
                Activity.RESULT_OK,
                PONativeAlternativePaymentMethodResult.Success
            )
            finish()
            return true
        }
        return false
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
