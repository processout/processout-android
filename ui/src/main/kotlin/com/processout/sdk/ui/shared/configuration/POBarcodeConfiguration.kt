package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POBarcodeConfiguration(
    val saveActionText: String? = null,
    val saveErrorConfirmation: POActionConfirmationConfiguration? = POActionConfirmationConfiguration()
) : Parcelable
