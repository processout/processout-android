package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PODialogStyle(
    val title: POTextStyle,
    val message: POTextStyle,
    val positiveButton: POButtonStyle,
    val negativeButton: POButtonStyle,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
