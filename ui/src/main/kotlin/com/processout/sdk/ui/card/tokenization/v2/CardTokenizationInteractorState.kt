package com.processout.sdk.ui.card.tokenization.v2

import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation

internal data class CardTokenizationInteractorState(
    val primaryActionId: String,
    val secondaryActionId: String,
    val focusedFieldId: String? = null,
    val submitting: Boolean = false,
    val issuerInformation: POCardIssuerInformation? = null,
    val preferredScheme: String? = null,
    val tokenizedCard: POCard? = null
)
