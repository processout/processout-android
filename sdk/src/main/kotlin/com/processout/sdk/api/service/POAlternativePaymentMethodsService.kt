package com.processout.sdk.api.service

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.ProcessOutResult

/**
 * Provides functionality related to alternative payment methods.
 */
interface POAlternativePaymentMethodsService {

    /**
     * Returns redirect URI for APM payments and APM token creation.
     */
    fun alternativePaymentMethodUri(request: POAlternativePaymentMethodRequest): ProcessOutResult<Uri>

    /**
     * Converts the given APM URI into response object.
     */
    fun alternativePaymentMethodResponse(uri: Uri): ProcessOutResult<POAlternativePaymentMethodResponse>
}
