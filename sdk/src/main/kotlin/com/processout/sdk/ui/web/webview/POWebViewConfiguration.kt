package com.processout.sdk.ui.web.webview

import android.net.Uri
import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.web.POActivityResultApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POWebViewConfiguration(
    val uri: Uri?,
    val returnUris: List<Uri>,
    val sdkVersion: String,
    val timeoutSeconds: Int?,
    val resultApi: POActivityResultApi = POActivityResultApi.Android
) : Parcelable
