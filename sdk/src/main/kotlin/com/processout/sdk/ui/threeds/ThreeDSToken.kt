package com.processout.sdk.ui.threeds

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ThreeDSToken(
    val token: String
) : Parcelable
