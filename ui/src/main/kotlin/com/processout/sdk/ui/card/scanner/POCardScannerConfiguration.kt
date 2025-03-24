package com.processout.sdk.ui.card.scanner

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.POBorderStyle
import com.processout.sdk.ui.core.style.POButtonStyle
import com.processout.sdk.ui.core.style.PODialogStyle
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/**
 * Specifies card scanner configuration.
 *
 * @param[title] Custom title.
 * @param[description] Custom description.
 * @param[cancelButton] Cancel button configuration. Pass _null_ to hide.
 * @param[cancellation] Specifies cancellation behaviour.
 * @param[shouldScanExpiredCard] Specifies whether the card scanner allows to scan expired cards.
 * Default value is _false_.
 * @param[style] Custom style.
 */
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

    /**
     * Specifies card scanner style.
     *
     * @param[title] Title style.
     * @param[description] Description style.
     * @param[cameraPreview] Camera preview style.
     * @param[card] Scanned card details style.
     * @param[torchToggle] Torch toggle button style.
     * @param[cancelButton] Cancel button style.
     * @param[dialog] Dialog style.
     * @param[backgroundColorResId] Color resource ID for background.
     */
    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val description: POTextStyle? = null,
        val cameraPreview: CameraPreviewStyle? = null,
        val card: CardStyle? = null,
        val torchToggle: POButtonStyle? = null,
        val cancelButton: POButtonStyle? = null,
        val dialog: PODialogStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    /**
     * Specifies the style of a camera preview.
     *
     * @param[border] Border style.
     * @param[overlayColorResId] Color resource ID for camera preview overlay.
     */
    @Parcelize
    data class CameraPreviewStyle(
        val border: POBorderStyle,
        @ColorRes
        val overlayColorResId: Int
    ) : Parcelable

    /**
     * Specifies the style of a scanned card details view.
     *
     * @param[number] Card number style.
     * @param[expiration] Card expiration style.
     * @param[cardholderName] Cardholder name style.
     * @param[border] Border style.
     */
    @Parcelize
    data class CardStyle(
        val number: POTextStyle,
        val expiration: POTextStyle,
        val cardholderName: POTextStyle,
        val border: POBorderStyle
    ) : Parcelable
}
