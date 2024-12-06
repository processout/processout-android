package com.processout.sdk.ui.web.customtab

import android.net.Uri
import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.web.POActivityResultApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POCustomTabConfiguration(
    val uri: Uri,
    val returnUri: Uri,
    val timeoutSeconds: Int?,
    val resultApi: POActivityResultApi = POActivityResultApi.Android
) : Parcelable
