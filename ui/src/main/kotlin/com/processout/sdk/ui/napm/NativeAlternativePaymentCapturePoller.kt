package com.processout.sdk.ui.napm

import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState.SUCCESS
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentTokenizationResponse
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.fold
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Tokenization
import kotlinx.coroutines.delay

internal class NativeAlternativePaymentCapturePoller(
    private val configuration: PONativeAlternativePaymentConfiguration,
    private val invoicesService: POInvoicesService,
    private val customerTokensService: POCustomerTokensService,
    private val retryStrategy: PORetryStrategy = Exponential(
        maxRetries = Int.MAX_VALUE,
        initialDelay = 150,
        minDelay = 3 * 1000,
        maxDelay = 90 * 1000,
        factor = 1.45
    )
) {

    data class CaptureResponse(
        val state: PONativeAlternativePaymentState,
        val elements: List<PONativeAlternativePaymentElement>?
    )

    private var startTimeMillis = 0L
    private var elapsedTimeMillis = 0L

    val isStarted: Boolean
        get() = startTimeMillis != 0L

    suspend fun start(): ProcessOutResult<CaptureResponse> {
        try {
            return poll()
        } finally {
            startTimeMillis = 0L
            elapsedTimeMillis = 0L
        }
    }

    private suspend fun poll(): ProcessOutResult<CaptureResponse> {
        startTimeMillis = System.currentTimeMillis()
        val iterator = retryStrategy.iterator
        while (elapsedTimeMillis <= configuration.paymentConfirmation.timeoutSeconds * 1000) {
            val result = call()
            POLogger.debug("Attempted to confirm the payment.")
            if (!isRetryable(result)) {
                return result
            }
            delay(timeMillis = iterator.next())
            elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
        }
        return ProcessOutResult.Failure(
            code = Timeout(),
            message = "Payment confirmation has timed out."
        )
    }

    private suspend fun call(): ProcessOutResult<CaptureResponse> =
        when (val flow = configuration.flow) {
            is Authorization -> invoicesService.authorize(
                request = PONativeAlternativePaymentAuthorizationRequest(
                    invoiceId = flow.invoiceId,
                    gatewayConfigurationId = flow.gatewayConfigurationId,
                    configuration = flow.configuration
                )
            ).map()
            is Tokenization -> customerTokensService.tokenize(
                request = PONativeAlternativePaymentTokenizationRequest(
                    customerId = flow.customerId,
                    customerTokenId = flow.customerTokenId,
                    gatewayConfigurationId = flow.gatewayConfigurationId,
                    configuration = flow.configuration
                )
            ).map()
        }

    private fun isRetryable(
        result: ProcessOutResult<CaptureResponse>
    ): Boolean = result.fold(
        onSuccess = { it.state != SUCCESS },
        onFailure = {
            val retryableCodes = listOf(
                NetworkUnreachable,
                Timeout(),
                Internal()
            )
            retryableCodes.contains(it.code)
        }
    )

    @JvmName(name = "mapFromAuthorizationResult")
    private fun ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse>.map() =
        fold(
            onSuccess = {
                ProcessOutResult.Success(
                    CaptureResponse(
                        state = it.state,
                        elements = it.elements
                    )
                )
            },
            onFailure = { it }
        )

    @JvmName(name = "mapFromTokenizationResult")
    private fun ProcessOutResult<PONativeAlternativePaymentTokenizationResponse>.map() =
        fold(
            onSuccess = {
                ProcessOutResult.Success(
                    CaptureResponse(
                        state = it.state,
                        elements = it.elements
                    )
                )
            },
            onFailure = { it }
        )
}
