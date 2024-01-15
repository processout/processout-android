package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POCardTokenizationResponse(
    val card: POCard,
    val formData: POCardTokenizationFormData
) : Parcelable
