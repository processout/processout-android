package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POStepperStyle(
    val pending: StepStyle,
    val active: StepStyle,
    val completed: StepStyle
) : Parcelable {

    @Parcelize
    data class StepStyle(
        val title: POTextStyle,
        val description: POTextStyle,
        val icon: IconStyle,
        val connector: POStrokeStyle
    ) : Parcelable

    @Parcelize
    data class IconStyle(
        @ColorRes
        val backgroundColorResId: Int,
        val border: Border?,
        val halo: Halo?,
        val checkmark: Checkmark?
    ) : Parcelable {

        @Parcelize
        data class Border(
            val widthDp: Int,
            @ColorRes
            val colorResId: Int
        ) : Parcelable

        @Parcelize
        data class Halo(
            val widthDp: Int,
            @ColorRes
            val colorResId: Int
        ) : Parcelable

        @Parcelize
        data class Checkmark(
            val widthDp: Int,
            @ColorRes
            val colorResId: Int
        ) : Parcelable
    }
}
