package com.processout.sdk.ui.web.customtab

import android.net.Uri

internal sealed class CustomTabAuthorizationUiState {
    object Initial : CustomTabAuthorizationUiState()
    data class Launching(val uri: Uri) : CustomTabAuthorizationUiState()
    object Launched : CustomTabAuthorizationUiState()
    data class Success(val returnUri: Uri) : CustomTabAuthorizationUiState()
    object Cancelled : CustomTabAuthorizationUiState()
    data class Timeout(val clearBackStack: Boolean) : CustomTabAuthorizationUiState()
}
