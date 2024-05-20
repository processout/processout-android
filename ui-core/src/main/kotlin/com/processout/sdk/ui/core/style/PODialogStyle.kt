package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PODialogStyle(
    val title: POTextStyle,
    val message: POTextStyle,
    val confirmButton: POButtonStyle,
    val dismissButton: POButtonStyle,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
