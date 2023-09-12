package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

/**
 * Provides functionality related to cards.
 */
interface POCardsRepository {

    /**
     * Tokenizes a card. You can use the card for a single payment by creating a card token with it.
     * If you want to use the card for multiple payments then you can use the card token to create a reusable customer token.
     * Note that once you have used the card token either for a payment or to create a customer token,
     * the card token becomes invalid and you cannot use it for any further actions.
     */
    suspend fun tokenize(request: POCardTokenizationRequest): ProcessOutResult<POCard>

    /**
     * Tokenizes a card. You can use the card for a single payment by creating a card token with it.
     * If you want to use the card for multiple payments then you can use the card token to create a reusable customer token.
     * Note that once you have used the card token either for a payment or to create a customer token,
     * the card token becomes invalid and you cannot use it for any further actions.
     */
    fun tokenize(
        request: POCardTokenizationRequest,
        callback: ProcessOutCallback<POCard>
    )

    /**
     * Updates card information.
     */
    suspend fun updateCVC(cardId: String, request: POCardUpdateCVCRequest): ProcessOutResult<POCard>

    /**
     * Updates card information.
     */
    fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest,
        callback: ProcessOutCallback<POCard>
    )

    /**
     * Allows to fetch card issuer information based on IIN.
     * @param iin Card issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
     */
    suspend fun fetchIssuerInformation(iin: String): ProcessOutResult<POCardIssuerInformation>

    /**
     * Allows to fetch card issuer information based on IIN.
     * @param iin Card issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
     */
    fun fetchIssuerInformation(
        iin: String,
        callback: ProcessOutCallback<POCardIssuerInformation>
    )
}
