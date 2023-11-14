package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCancellationConfiguration(
    val dragDown: Boolean = true,
    val touchOutside: Boolean = true,
    val backPressed: Boolean = true
) : Parcelable
