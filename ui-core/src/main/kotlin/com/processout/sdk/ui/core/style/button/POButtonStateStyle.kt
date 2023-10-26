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
    val paddingHorizontalDp: Int = POButtonDefaults.PADDING_HORIZONTAL_DP,
    val paddingVerticalDp: Int = POButtonDefaults.PADDING_VERTICAL_DP
) : Parcelable
