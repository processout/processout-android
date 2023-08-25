package com.processout.sdk.ui.web.customtab

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_TIMEOUT_FINISH
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

internal class CustomTabAuthorizationViewModel(
    private val configuration: CustomTabConfiguration
) : ViewModel() {

    internal class Factory(
        private val configuration: CustomTabConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CustomTabAuthorizationViewModel(configuration) as T
    }

    private val _uiState = MutableStateFlow<CustomTabAuthorizationUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    private val timeoutHandler by lazy { Handler(Looper.getMainLooper()) }

    init {
        configuration.timeoutSeconds?.let {
            timeoutHandler.postDelayed(
                { _uiState.value = Timeout(clearBackStack = true) },
                TimeUnit.SECONDS.toMillis(it.toLong())
            )
        }
    }

    fun onResume(intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_TIMEOUT_FINISH, false)) {
            _uiState.value = Timeout(clearBackStack = false)
            return
        }
        val uiState = _uiState.value
        if (uiState == Initial) {
            _uiState.value = Launching(configuration.uri)
            return
        }
        val returnUri = intent.data
        if (returnUri != null) {
            POLogger.info("Custom Chrome Tabs has been redirected to return URL: %s", returnUri)
            _uiState.value = Success(returnUri)
            return
        }
        when (uiState) {
            is Launching, Launched -> _uiState.value = Cancelled
            else -> {}
        }
    }

    fun onLaunched() {
        _uiState.value = Launched
        POLogger.info("Custom Chrome Tabs has launched URL: %s", configuration.uri)
    }

    override fun onCleared() {
        super.onCleared()
        timeoutHandler.removeCallbacksAndMessages(null)
    }
}
