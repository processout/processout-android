package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.button.POButtonStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POActionsContainerStyle(
    val primary: POButtonStyle,
    val secondary: POButtonStyle,
    val axis: POAxis,
    @ColorRes
    val dividerColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
