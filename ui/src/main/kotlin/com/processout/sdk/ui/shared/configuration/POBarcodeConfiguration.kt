package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/**
 * Specifies barcode configuration.
 *
 * @param[saveActionText] Text on the save button.
 * @param[saveButton] Save button configuration.
 * @param[saveErrorConfirmation] Requests user confirmation (e.g. dialog) when barcode saving has failed. Use _null_ to disable.
 */
@Parcelize
data class POBarcodeConfiguration @ProcessOutInternalApi constructor(
    @Deprecated(message = "Use 'saveButton.text' property.")
    val saveActionText: String? = null,
    val saveButton: Button = Button(),
    val saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
) : Parcelable {

    @Deprecated(message = "Use constructor POBarcodeConfiguration(saveButton, saveErrorConfirmation)")
    constructor(
        saveActionText: String? = null,
        saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
    ) : this(
        saveActionText = saveActionText,
        saveButton = Button(text = saveActionText),
        saveErrorConfirmation = saveErrorConfirmation
    )

    constructor(
        saveButton: Button = Button(),
        saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
    ) : this(
        saveActionText = saveButton.text,
        saveButton = saveButton,
        saveErrorConfirmation = saveErrorConfirmation
    )

    /**
     * Button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[iconResId] Button icon drawable resource ID. Pass _null_ to hide.
     */
    @Parcelize
    data class Button(
        val text: String? = null,
        @DrawableRes
        val iconResId: Int? = null
    ) : Parcelable
}
