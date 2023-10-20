package com.processout.sdk.ui.core.style.button

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.POBorderStyle
import com.processout.sdk.ui.core.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStateStyle(
    val text: POTextStyle,
    val border: POBorderStyle,
    @ColorRes
    val backgroundColorResId: Int,
    val elevationDp: Int,
    val paddingDp: Int = DEFAULT_PADDING
) : Parcelable {
    companion object {
        const val DEFAULT_PADDING = 22
    }
}
