package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface POCardsRepository {

    suspend fun tokenize(request: POCardTokenizationRequest): ProcessOutResult<POCard>
    fun tokenize(
        request: POCardTokenizationRequest,
        callback: ProcessOutCallback<POCard>
    )

    suspend fun updateCVC(cardId: String, request: POCardUpdateCVCRequest): ProcessOutResult<POCard>
    fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest,
        callback: ProcessOutCallback<POCard>
    )

    /**
     * Allows to fetch card issuer information based on iin.
     * @param iin Card issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
     */
    suspend fun fetchIssuerInformation(iin: String): ProcessOutResult<POCardIssuerInformation>

    /**
     * Allows to fetch card issuer information based on iin.
     * @param iin Card issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
     */
    fun fetchIssuerInformation(
        iin: String,
        callback: ProcessOutCallback<POCardIssuerInformation>
    )
}
