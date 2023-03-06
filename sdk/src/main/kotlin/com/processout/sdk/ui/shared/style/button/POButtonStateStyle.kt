package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStateStyle(
    val text: POTextStyle,
    val border: POBorderStyle,
    @ColorInt
    val backgroundColor: Int,
    val elevationDp: Int,
    val paddingDp: Int = DEFAULT_PADDING
) : Parcelable {
    companion object {
        const val DEFAULT_PADDING = 22
    }
}
