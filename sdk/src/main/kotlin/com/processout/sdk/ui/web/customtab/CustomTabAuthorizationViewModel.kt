package com.processout.sdk.ui.web.customtab

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_TIMEOUT_FINISH
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Cancelled
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Initial
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Launched
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Launching
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Success
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.Timeout
import java.util.concurrent.TimeUnit

internal class CustomTabAuthorizationViewModel(
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
    }

    val uiState = savedState.getStateFlow<CustomTabAuthorizationUiState>(KEY_SAVED_STATE, Initial)

    private val timeoutHandler by lazy { Handler(Looper.getMainLooper()) }

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
            POLogger.info("Custom Chrome Tabs has been redirected to return URL: %s", returnUri)
            savedState[KEY_SAVED_STATE] = Success(returnUri)
            return
        }
        when (uiState) {
            is Launching, Launched -> savedState[KEY_SAVED_STATE] = Cancelled
            else -> {}
        }
    }

    fun onLaunched() {
        savedState[KEY_SAVED_STATE] = Launched
        POLogger.info("Custom Chrome Tabs has launched URL: %s", configuration.uri)
    }

    override fun onCleared() {
        super.onCleared()
        timeoutHandler.removeCallbacksAndMessages(null)
    }
}
