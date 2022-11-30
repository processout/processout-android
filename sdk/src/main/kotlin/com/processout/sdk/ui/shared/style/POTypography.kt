package com.processout.sdk.ui.shared.style

import android.os.Parcelable
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTypography(
    @FontRes
    val fontResId: Int? = null
) : Parcelable {

    companion object {
        val default = POTypography()
    }
}
