package com.processout.sdk.api.provider

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.ProcessOutResult

interface AlternativePaymentMethodProvider {
    fun alternativePaymentMethodURL(request: POAlternativePaymentMethodRequest): ProcessOutResult<Uri>

    fun alternativePaymentMethodResponse(uri: Uri): ProcessOutResult<POAlternativePaymentMethodResponse>
}
