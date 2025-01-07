package com.processout.sdk.ui.napm

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.api.dispatcher.PODefaultEventDispatchers
import com.processout.sdk.core.*
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Success
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Dismiss
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.PermissionRequestResult
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.PermissionRequest
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Capture
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.SubmitButton
import com.processout.sdk.ui.shared.component.isImeVisibleAsState
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import com.processout.sdk.ui.shared.extension.collectImmediately
import com.processout.sdk.ui.shared.extension.dpToPx
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

internal class NativeAlternativePaymentBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = NativeAlternativePaymentBottomSheet::class.java.simpleName
    }

    override val expandable = true
    override val defaultViewHeight by lazy { 460.dpToPx(requireContext()) }
    private val maxPeekHeight by lazy { (screenHeight * 0.8).roundToInt() }

    private var configuration: PONativeAlternativePaymentConfiguration? = null

    private val viewModel: NativeAlternativePaymentViewModel by viewModels {
        NativeAlternativePaymentViewModel.Factory(
            app = requireActivity().application,
            invoiceId = configuration?.invoiceId ?: String(),
            gatewayConfigurationId = configuration?.gatewayConfigurationId ?: String(),
            options = configuration?.options ?: Options(submitButton = SubmitButton()),
            eventDispatcher = PODefaultEventDispatchers.defaultNativeAlternativePaymentMethod
        )
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::handlePermissions
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
        configuration?.run {
            if (invoiceId.isBlank() || gatewayConfigurationId.isBlank()) {
                dismiss(
                    ProcessOutResult.Failure(
                        code = POFailure.Code.Generic(),
                        message = "Invalid configuration."
                    )
                )
                return
            }
        }
        viewModel.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ProcessOutTheme {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                viewModel.sideEffects.collectImmediately { handle(it) }

                val state by viewModel.state.collectAsStateWithLifecycle()
                val isImeVisible by isImeVisibleAsState()
                var isContentHeightIncreased by remember { mutableStateOf(false) }
                var viewHeight by remember { mutableIntStateOf(defaultViewHeight) }
                if (state is Capture && !isImeVisible) {
                    LaunchedEffect(true) {
                        delay(AnimationDurationMillis.toLong())
                        viewHeight = maxPeekHeight
                    }
                }
                with(screenModeAsState(viewHeight = viewHeight)) {
                    LaunchedEffect(value) {
                        apply(
                            screenMode = value,
                            animate = isContentHeightIncreased || state is Capture
                        )
                        isContentHeightIncreased = false
                    }
                }

                NativeAlternativePaymentScreen(
                    state = state,
                    onEvent = remember { viewModel::onEvent },
                    onContentHeightChanged = { contentHeight ->
                        if (contentHeight > viewHeight) {
                            isContentHeightIncreased = true
                            viewHeight = contentHeight
                        }
                    },
                    style = NativeAlternativePaymentScreen.style(custom = configuration?.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.options?.cancellation?.let {
            apply(
                POCancellationConfiguration(
                    backPressed = it.backPressed,
                    dragDown = it.dragDown,
                    touchOutside = it.touchOutside
                )
            )
        }
    }

    private fun handle(sideEffect: NativeAlternativePaymentSideEffect) {
        when (sideEffect) {
            is PermissionRequest -> when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    sideEffect.permission
                ) == PackageManager.PERMISSION_GRANTED ->
                    viewModel.onEvent(
                        PermissionRequestResult(
                            permission = sideEffect.permission,
                            isGranted = true
                        )
                    )
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    sideEffect.permission
                ) -> viewModel.onEvent(
                    PermissionRequestResult(
                        permission = sideEffect.permission,
                        isGranted = false
                    )
                )
                else -> permissionsLauncher.launch(arrayOf(sideEffect.permission))
            }
        }
    }

    private fun handlePermissions(result: Map<String, Boolean>) {
        result.forEach {
            viewModel.onEvent(PermissionRequestResult(permission = it.key, isGranted = it.value))
        }
    }

    private fun handle(completion: NativeAlternativePaymentCompletion) =
        when (completion) {
            Success -> finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(POUnit)
            )
            is Failure -> finishWithActivityResult(
                resultCode = Activity.RESULT_CANCELED,
                result = completion.failure.toActivityResult()
            )
            else -> {}
        }

    override fun onCancellation(failure: ProcessOutResult.Failure) = dismiss(failure)

    private fun dismiss(failure: ProcessOutResult.Failure) {
        viewModel.onEvent(Dismiss(failure))
        val isCaptured = when (val state = viewModel.state.value) {
            is Capture -> state.isCaptured
            else -> false
        }
        if (isCaptured) {
            finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(POUnit)
            )
        } else {
            finishWithActivityResult(
                resultCode = Activity.RESULT_CANCELED,
                result = failure.toActivityResult()
            )
        }
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POUnit>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
