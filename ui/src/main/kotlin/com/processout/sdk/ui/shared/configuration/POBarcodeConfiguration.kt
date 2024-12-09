package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Specifies barcode configuration.
 *
 * @param[saveActionText] Text on the button that saves barcode.
 * @param[saveErrorConfirmation] Requests user confirmation (e.g. dialog) when saving barcode has failed. Use _null_ to disable.
 */
@Parcelize
data class POBarcodeConfiguration(
    val saveActionText: String? = null,
    val saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
) : Parcelable
