package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/**
 * Defines card tokenization configuration.
 *
 * @param[title] Custom title.
 * @param[cvcRequired] Specifies whether the CVC field should be displayed. Default value is _true_.
 * @param[cardholderNameRequired] Specifies whether the cardholder name field should be displayed. Default value is _true_.
 * @param[billingAddress] Allows to customize the collection of billing address.
 * @param[savingAllowed] Displays checkbox that allows to save the card details for future payments.
 * @param[submitButton] Submit button configuration.
 * @param[cancelButton] Cancel button configuration. Use _null_ to hide.
 * @param[cancellation] Specifies cancellation behaviour.
 * @param[metadata] Metadata related to the card.
 * @param[style] Allows to customize the look and feel.
 */
@Parcelize
data class POCardTokenizationConfiguration(
    val title: String? = null,
    val cvcRequired: Boolean = true,
    val cardholderNameRequired: Boolean = true,
    val billingAddress: BillingAddressConfiguration = BillingAddressConfiguration(),
    val savingAllowed: Boolean = false,
    val submitButton: Button = Button(),
    val cancelButton: CancelButton? = CancelButton(),
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val metadata: Map<String, String>? = null,
    val style: Style? = null
) : Parcelable {

    /**
     * Defines card tokenization configuration.
     *
     * @param[title] Custom title.
     * @param[cvcRequired] Specifies whether the CVC field should be displayed. Default value is _true_.
     * @param[isCardholderNameFieldVisible] Specifies whether the cardholder name field should be displayed. Default value is _true_.
     * @param[billingAddress] Allows to customize the collection of billing address.
     * @param[savingAllowed] Displays checkbox that allows to save the card details for future payments.
     * @param[primaryActionText] Custom primary action text (e.g. "Submit").
     * @param[secondaryActionText] Custom secondary action text (e.g. "Cancel").
     * @param[cancellation] Specifies cancellation behaviour.
     * @param[metadata] Metadata related to the card.
     * @param[style] Allows to customize the look and feel.
     */
    @Deprecated(message = "Use alternative constructor.")
    constructor(
        title: String? = null,
        cvcRequired: Boolean = true,
        isCardholderNameFieldVisible: Boolean = true,
        billingAddress: BillingAddressConfiguration = BillingAddressConfiguration(),
        savingAllowed: Boolean = false,
        primaryActionText: String? = null,
        secondaryActionText: String? = null,
        cancellation: POCancellationConfiguration = POCancellationConfiguration(),
        metadata: Map<String, String>? = null,
        style: Style? = null
    ) : this(
        title = title,
        cvcRequired = cvcRequired,
        cardholderNameRequired = isCardholderNameFieldVisible,
        billingAddress = billingAddress,
        savingAllowed = savingAllowed,
        submitButton = Button(text = primaryActionText),
        cancelButton = if (cancellation.secondaryAction)
            CancelButton(text = secondaryActionText) else null,
        cancellation = cancellation,
        metadata = metadata,
        style = style
    )

    /**
     * Defines billing address configuration.
     *
     * @param[mode] Defines how to collect the billing address. Default value is [CollectionMode.Automatic].
     * @param[countryCodes] Set of ISO country codes that is supported for the billing address. When _null_, all countries are provided.
     * @param[defaultAddress] Default address information.
     * @param[attachDefaultsToPaymentMethod] Specifies whether the [defaultAddress] values should be attached to the card,
     * including fields that aren't displayed in the form.
     */
    @Parcelize
    data class BillingAddressConfiguration(
        val mode: CollectionMode = CollectionMode.Automatic,
        val countryCodes: Set<String>? = null,
        val defaultAddress: POContact? = null,
        val attachDefaultsToPaymentMethod: Boolean = false
    ) : Parcelable {

        /**
         * Defines how to collect the billing address.
         */
        @Parcelize
        enum class CollectionMode : Parcelable {
            /** Never collect. */
            Never,

            /** Only collect the fields that are required by the particular payment method. */
            Automatic,

            /** Collect the full billing address. */
            Full
        }
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
     * Allows to customize the look and feel.
     *
     * @param[title] Title style.
     * @param[sectionTitle] Section title style.
     * @param[field] Field style.
     * @param[checkbox] Checkbox style.
     * @param[dropdownMenu] Dropdown menu style.
     * @param[errorMessage] Error message style.
     * @param[actionsContainer] Style of action buttons and their container.
     * @param[backgroundColorResId] Color resource ID for background.
     * @param[dividerColorResId] Color resource ID for title divider.
     * @param[dragHandleColorResId] Color resource ID for bottom sheet drag handle.
     */
    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val sectionTitle: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val checkbox: POCheckboxStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
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
