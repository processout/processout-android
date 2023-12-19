package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import com.processout.sdk.api.model.response.POCard
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCardTokenizationResponse(
    val card: POCard,
    val formData: POCardTokenizationFormData
) : Parcelable
