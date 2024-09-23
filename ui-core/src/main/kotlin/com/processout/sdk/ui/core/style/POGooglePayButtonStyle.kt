package com.processout.sdk.ui.core.style

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POGooglePayButtonStyle(
    val type: Type,
    val theme: Theme,
    val heightDp: Int,
    val borderRadiusDp: Int
) : Parcelable {

    @Parcelize
    enum class Type : Parcelable {
        BUY,
        BOOK,
        CHECKOUT,
        DONATE,
        ORDER,
        PAY,
        SUBSCRIBE,
        PLAIN
    }

    @Parcelize
    enum class Theme : Parcelable {
        DARK, LIGHT, AUTOMATIC
    }
}
