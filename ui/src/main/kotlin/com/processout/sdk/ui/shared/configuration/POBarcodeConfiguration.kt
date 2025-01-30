package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import kotlinx.parcelize.Parcelize

/**
 * Specifies barcode configuration.
 *
 * @param[saveButton] Save button configuration.
 * @param[saveErrorConfirmation] Requests user confirmation (e.g. dialog) when barcode saving has failed. Use _null_ to disable.
 */
@Parcelize
data class POBarcodeConfiguration(
    val saveButton: Button = Button(),
    val saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
) : Parcelable {

    /**
     * Specifies barcode configuration.
     *
     * @param[saveActionText] Save button text.
     * @param[saveErrorConfirmation] Requests user confirmation (e.g. dialog) when barcode saving has failed. Use _null_ to disable.
     */
    @Deprecated(message = "Use alternative constructor.")
    constructor(
        saveActionText: String? = null,
        saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
    ) : this(
        saveButton = Button(text = saveActionText),
        saveErrorConfirmation = saveErrorConfirmation
    )

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
}
