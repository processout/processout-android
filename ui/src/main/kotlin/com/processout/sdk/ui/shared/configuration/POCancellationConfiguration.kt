package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Specifies cancellation behaviour.
 *
 * @param[secondaryAction] Enables secondary cancel action button. Default value is _true_.
 * @param[backPressed] Cancel on back button press or back gesture. Default value is _true_.
 * @param[dragDown] Cancel when bottom sheet is dragged down out of the screen. Default value is _true_.
 * @param[touchOutside] Cancel on touch of the outside dimmed area of the bottom sheet. Default value is _true_.
 */
@Parcelize
data class POCancellationConfiguration(
    val secondaryAction: Boolean = true,
    val backPressed: Boolean = true,
    val dragDown: Boolean = true,
    val touchOutside: Boolean = true
) : Parcelable
