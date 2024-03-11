package com.processout.sdk.ui.web.customtab

import android.net.Uri
import android.os.Parcelable
import com.processout.sdk.ui.web.ActivityResultApi
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CustomTabConfiguration(
    val uri: Uri,
    val returnUri: Uri,
    val timeoutSeconds: Int?,
    val resultApi: ActivityResultApi = ActivityResultApi.Android
) : Parcelable
