package com.processout.sdk.ui.card.scanner

import android.os.Parcelable
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.POButtonStyle
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCardScannerConfiguration(
    val title: String? = null,
    val description: String? = null,
    val cancelButton: CancelButton? = CancelButton(),
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val shouldScanExpiredCard: Boolean = false,
    val style: Style? = null
) : Parcelable {

    /**
     * Cancel button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
     * Use _null_ to disable, this is a default behaviour.
     */
    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val description: POTextStyle? = null,
        val cancelButton: POButtonStyle? = null
    ) : Parcelable
}
