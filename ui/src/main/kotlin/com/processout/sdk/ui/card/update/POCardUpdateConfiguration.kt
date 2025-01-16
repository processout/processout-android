package com.processout.sdk.ui.card.update

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/**
 * Defines card update configuration.
 *
 * @param[cardId] Card ID.
 * @param[options] Allows to customize behaviour and pre-define the values.
 * @param[style] Allows to customize the look and feel.
 */
@Parcelize
data class POCardUpdateConfiguration(
    val cardId: String,
    val options: Options = Options(submitButton = Button()),
    val style: Style? = null
) : Parcelable {

    /**
     * Allows to customize behaviour and pre-define the values.
     *
     * @param[title] Custom title.
     * @param[cardInformation] Allows to provide card information that will be visible in UI.
     * @param[submitButton] Submit button configuration.
     * @param[cancelButton] Cancel button configuration. Use _null_ to hide.
     * @param[cancellation] Specifies cancellation behaviour.
     */
    @Parcelize
    data class Options(
        val title: String? = null,
        val cardInformation: CardInformation? = null,
        val submitButton: Button = Button(),
        val cancelButton: CancelButton? = CancelButton(),
        val cancellation: POCancellationConfiguration = POCancellationConfiguration()
    ) : Parcelable {

        /**
         * Allows to customize behaviour and pre-define the values.
         *
         * @param[title] Custom title.
         * @param[cardInformation] Allows to provide card information that will be visible in UI.
         * @param[primaryActionText] Custom primary action text (e.g. "Submit").
         * @param[secondaryActionText] Custom secondary action text (e.g. "Cancel").
         * @param[cancellation] Specifies cancellation behaviour.
         */
        @Deprecated(message = "Use alternative constructor.")
        constructor(
            title: String? = null,
            cardInformation: CardInformation? = null,
            primaryActionText: String? = null,
            secondaryActionText: String? = null,
            cancellation: POCancellationConfiguration = POCancellationConfiguration()
        ) : this(
            title = title,
            cardInformation = cardInformation,
            submitButton = Button(text = primaryActionText),
            cancelButton = if (cancellation.secondaryAction)
                CancelButton(text = secondaryActionText) else null,
            cancellation = cancellation
        )
    }

    /**
     * Button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     */
    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null
    ) : Parcelable

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
     * Allows to provide card information that will be visible in UI.
     *
     * @param[maskedNumber] Masked card number displayed to user as is.
     * @param[iin] Card issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
     * When this property is _null_ implementation will attempt to extract IIN from [maskedNumber].
     * You may want to set this property explicitly if IIN is hidden in masked number.
     * @param[scheme] Scheme of the card.
     * @param[preferredScheme] Preferred scheme of the card previously selected by the user if any.
     */
    @Parcelize
    data class CardInformation(
        val maskedNumber: String? = null,
        val iin: String? = null,
        val scheme: String? = null,
        val preferredScheme: String? = null
    ) : Parcelable

    /**
     * Allows to customize the look and feel.
     *
     * @param[title] Title style.
     * @param[field] Field style.
     * @param[errorMessage] Error message style.
     * @param[actionsContainer] Style of action buttons and their container.
     * @param[backgroundColorResId] Color resource ID for background.
     * @param[dividerColorResId] Color resource ID for title divider.
     * @param[dragHandleColorResId] Color resource ID for bottom sheet drag handle.
     */
    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val errorMessage: POTextStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null
    ) : Parcelable
}
