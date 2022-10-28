package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface CardsRepository {

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
}
