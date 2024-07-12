package com.processout.sdk.ui.web.customtab

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_TIMEOUT_FINISH
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.*
import java.util.concurrent.TimeUnit

internal class CustomTabAuthorizationViewModel private constructor(
    private val savedState: SavedStateHandle,
    private val configuration: CustomTabConfiguration
) : ViewModel() {

    internal class Factory(
        owner: SavedStateRegistryOwner,
        private val configuration: CustomTabConfiguration
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs = null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
            CustomTabAuthorizationViewModel(handle, configuration) as T
    }

    companion object {
        private const val KEY_SAVED_STATE = "CustomTabAuthorizationUiState"
        private const val CANCELLATION_DELAY_MS = 700L
    }

    val uiState = savedState.getStateFlow<CustomTabAuthorizationUiState>(KEY_SAVED_STATE, Initial)

    private val timeoutHandler by lazy { Handler(Looper.getMainLooper()) }

    private val cancellationRunnable = Runnable { savedState[KEY_SAVED_STATE] = Cancelled }

    init {
        configuration.timeoutSeconds?.let {
            timeoutHandler.postDelayed(
                { savedState[KEY_SAVED_STATE] = Timeout(clearBackStack = true) },
                TimeUnit.SECONDS.toMillis(it.toLong())
            )
        }
    }

    fun onResume(intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_TIMEOUT_FINISH, false)) {
            savedState[KEY_SAVED_STATE] = Timeout(clearBackStack = false)
            return
        }
        val uiState = savedState.get<CustomTabAuthorizationUiState>(KEY_SAVED_STATE)
        if (uiState == Initial) {
            savedState[KEY_SAVED_STATE] = Launching(configuration.uri)
            return
        }
        val returnUri = intent.data
        if (returnUri != null) {
            timeoutHandler.removeCallbacks(cancellationRunnable)
            if (returnUri.scheme == configuration.returnUri.scheme &&
                returnUri.host == configuration.returnUri.host &&
                returnUri.path == configuration.returnUri.path
            ) {
                POLogger.info("Custom Chrome Tabs has been redirected to return URI: %s", returnUri)
                savedState[KEY_SAVED_STATE] = Success(returnUri)
            } else {
                val errorMessage = "Unexpected Custom Chrome Tabs redirect to URI: $returnUri"
                POLogger.error(errorMessage)
                savedState[KEY_SAVED_STATE] = Failure(
                    ProcessOutActivityResult.Failure(
                        code = POFailure.Code.Internal(),
                        message = errorMessage
                    )
                )
            }
            return
        }
        when (uiState) {
            is Launching, Launched ->
                // Delay cancellation as return URI still can be received by a deep link shortly in the following scenario:
                // 1) Activity and ViewModel has been destroyed while user is on the Custom Tab.
                // 2) User has left Custom Tab immediately after passing the challenge (phone call, other app, home screen, etc...)
                // and then goes back after redirect occurred. In this case the deep link will not be sent automatically and
                // with some Chrome versions it will show a default dialog to confirm redirection to the app.
                // 3) When user confirms redirect through this dialog first it will re-create Activity and ViewModel,
                // which is in the Launched state. Normally it would trigger cancellation as user goes back from the Custom Tab,
                // but in this case we will also receive a deep link shortly after that.
                timeoutHandler.postDelayed(cancellationRunnable, CANCELLATION_DELAY_MS)
            else -> {}
        }
    }

    fun onLaunched() {
        savedState[KEY_SAVED_STATE] = Launched
        POLogger.info("Custom Chrome Tabs has launched URL: %s", configuration.uri)
    }

    override fun onCleared() {
        timeoutHandler.removeCallbacksAndMessages(null)
    }
}
