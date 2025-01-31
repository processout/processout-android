package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import androidx.annotation.FloatRange
import kotlinx.parcelize.Parcelize

/**
 * Specifies bottom sheet configuration.
 *
 * @param[height] Specifies bottom sheet height.
 * @param[expandable] Specifies whether the bottom sheet is expandable.
 * @param[cancellation] Specifies cancellation behaviour.
 */
@Parcelize
data class POBottomSheetConfiguration(
    val height: Height,
    val expandable: Boolean,
    val cancellation: POCancellationConfiguration = POCancellationConfiguration()
) : Parcelable {

    /**
     * Specifies bottom sheet height.
     */
    sealed class Height : Parcelable {
        /**
         * Bottom sheet height will be fixed to the [fraction] of the screen height, between `0.5` and `1`, inclusive.
         */
        @Parcelize
        data class Fixed(
            @FloatRange(from = 0.5, to = 1.0)
            val fraction: Float
        ) : Height()

        /**
         * Bottom sheet height will change dynamically to wrap its content.
         */
        @Parcelize
        data object WrapContent : Height()
    }
}
