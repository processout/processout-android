package com.processout.sdk.ui.web

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CustomTabConfiguration(
    val uri: Uri,
    val timeoutSeconds: Int? = null
) : Parcelable
