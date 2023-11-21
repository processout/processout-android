package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCancellationConfiguration(
    val secondaryAction: Boolean = true,
    val backPressed: Boolean = true,
    val dragDown: Boolean = true,
    val touchOutside: Boolean = true
) : Parcelable
