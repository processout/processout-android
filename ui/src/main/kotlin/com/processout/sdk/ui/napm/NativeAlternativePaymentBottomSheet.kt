package com.processout.sdk.ui.napm

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.apm.POAlternativePaymentMethodCustomTabLauncher
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Success
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.PermissionRequest
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.Redirect
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Loaded
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Stage
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen
import com.processout.sdk.ui.shared.component.displayCutoutHeight
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.Fixed
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import com.processout.sdk.ui.shared.extension.collectImmediately
import com.processout.sdk.ui.shared.extension.dpToPx
import kotlin.math.roundToInt

internal class NativeAlternativePaymentBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = NativeAlternativePaymentBottomSheet::class.java.simpleName
    }

    override var expandable = true
    override val defaultViewHeight by lazy { 410.dpToPx(requireContext()) }

    private lateinit var configuration: PONativeAlternativePaymentConfiguration
    private val viewHeightConfiguration by lazy { configuration.bottomSheet.height }

    private val viewModel: NativeAlternativePaymentViewModel by viewModels {
        NativeAlternativePaymentViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration
        )
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::handlePermissions
    )

    private lateinit var alternativePaymentLauncher: POAlternativePaymentMethodCustomTabLauncher

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
            ?: PONativeAlternativePaymentConfiguration(
                flow = Flow.Authorization(
                    invoiceId = String(),
                    gatewayConfigurationId = String()
                ),
                redirect = null
            )
        alternativePaymentLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
            from = this,
            callback = { result ->
                viewModel.onEvent(RedirectResult(result))
            }
        )
        viewModel.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val isLightTheme = !isSystemInDarkTheme()
            ProcessOutTheme(isLightTheme = isLightTheme) {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                viewModel.sideEffects.collectImmediately { handle(it) }

                val displayCutoutHeight = displayCutoutHeight()
                val defaultViewHeight = when (val height = viewHeightConfiguration) {
                    is Fixed -> (screenHeight * height.fraction + displayCutoutHeight).roundToInt()
                    WrapContent -> defaultViewHeight + displayCutoutHeight
                }
                var viewHeight by remember { mutableIntStateOf(defaultViewHeight) }
                with(screenModeAsState(viewHeight = viewHeight)) {
                    LaunchedEffect(value) { apply(value) }
                }

                NativeAlternativePaymentScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    onContentHeightChanged = { contentHeight ->
                        if (viewHeightConfiguration is WrapContent) {
                            viewHeight = contentHeight.coerceAtLeast(defaultViewHeight)
                        }
                    },
                    isLightTheme = isLightTheme,
                    style = NativeAlternativePaymentScreen.style(custom = configuration.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandable = configuration.bottomSheet.expandable
        apply(configuration.bottomSheet.cancellation)
    }

    private fun handle(sideEffect: NativeAlternativePaymentSideEffect) {
        when (sideEffect) {
            is PermissionRequest -> requestPermission(sideEffect.permission)
            is Redirect -> alternativePaymentLauncher.launch(
                uri = sideEffect.redirectUrl.toUri(),
                returnUrl = sideEffect.returnUrl
            )
        }
    }

    private fun requestPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED ->
                viewModel.onEvent(
                    PermissionRequestResult(
                        permission = permission,
                        isGranted = true
                    )
                )
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                permission
            ) -> viewModel.onEvent(
                PermissionRequestResult(
                    permission = permission,
                    isGranted = false
                )
            )
            else -> permissionsLauncher.launch(arrayOf(permission))
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
        val isCompleted = when (val state = viewModel.state.value) {
            is Loaded -> state.content.stage is Stage.Completed
            else -> false
        }
        if (isCompleted) {
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
