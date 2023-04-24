package com.processout.sdk.ui.apm

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.service.AlternativePaymentMethodsService
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebViewDelegate

internal class AlternativePaymentMethodWebViewDelegate(
    private val service: AlternativePaymentMethodsService,
    private val request: POAlternativePaymentMethodRequest,
    private val callback: ((ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit)
) : WebViewDelegate {

    override val uri: Uri
        get() = when (val result = service.alternativePaymentMethodUri(request)) {
            is ProcessOutResult.Success -> result.value
            is ProcessOutResult.Failure -> Uri.EMPTY
        }

    override fun complete(uri: Uri) {
        when (val result = service.alternativePaymentMethodResponse(uri)) {
            is ProcessOutResult.Success -> callback(ProcessOutResult.Success(result.value))
            is ProcessOutResult.Failure -> callback(result.copy())
        }
    }

    override fun complete(failure: ProcessOutResult.Failure) {
        callback(failure.copy())
    }
}
