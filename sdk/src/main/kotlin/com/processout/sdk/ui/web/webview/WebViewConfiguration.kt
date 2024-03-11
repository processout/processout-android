package com.processout.sdk.ui.web.webview

import android.net.Uri
import android.os.Parcelable
import com.processout.sdk.ui.web.ActivityResultApi
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class WebViewConfiguration(
    val uri: Uri?,
    val returnUris: List<Uri>,
    val sdkVersion: String,
    val timeoutSeconds: Int?,
    val resultApi: ActivityResultApi = ActivityResultApi.Android
) : Parcelable
