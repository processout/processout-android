package com.processout.sdk.ui.web.customtab

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed class CustomTabAuthorizationUiState : Parcelable {
    @Parcelize
    data object Initial : CustomTabAuthorizationUiState()

    @Parcelize
    data class Launching(val uri: Uri) : CustomTabAuthorizationUiState()

    @Parcelize
    data object Launched : CustomTabAuthorizationUiState()

    @Parcelize
    data class Success(val returnUri: Uri) : CustomTabAuthorizationUiState()

    @Parcelize
    data object Cancelled : CustomTabAuthorizationUiState()

    @Parcelize
    data class Timeout(val clearBackStack: Boolean) : CustomTabAuthorizationUiState()
}
