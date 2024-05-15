package com.processout.sdk.ui.shared.style.dialog

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PODialogStyle(
    val title: POTextStyle,
    val message: POTextStyle,
    val positiveButton: POButtonStyle,
    val negativeButton: POButtonStyle,
    @ColorInt
    val backgroundColor: Int
) : Parcelable
