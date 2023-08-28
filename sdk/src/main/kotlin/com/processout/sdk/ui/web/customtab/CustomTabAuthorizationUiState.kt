package com.processout.sdk.ui.web.customtab

import android.net.Uri

internal sealed class CustomTabAuthorizationUiState {
    data object Initial : CustomTabAuthorizationUiState()
    data class Launching(val uri: Uri) : CustomTabAuthorizationUiState()
    data object Launched : CustomTabAuthorizationUiState()
    data class Success(val returnUri: Uri) : CustomTabAuthorizationUiState()
    data object Cancelled : CustomTabAuthorizationUiState()
    data class Timeout(val clearBackStack: Boolean) : CustomTabAuthorizationUiState()
}
