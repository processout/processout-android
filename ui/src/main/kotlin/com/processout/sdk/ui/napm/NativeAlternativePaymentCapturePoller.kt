@file:OptIn(ExperimentalCoroutinesApi::class)

package com.processout.sdk.ui.napm

import android.os.SystemClock
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlin.math.min

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

    private var backoffIterator = retryStrategy.newIterator()
    private val backoffResetSignal = Channel<Unit>(capacity = Channel.CONFLATED)

    suspend fun poll(): ProcessOutResult<CaptureResponse> {
        val timeout = configuration.paymentConfirmation.timeoutSeconds * 1000L
        val startTime = SystemClock.elapsedRealtime()
        backoffIterator = retryStrategy.newIterator()
        while (backoffResetSignal.tryReceive().isSuccess) {
            // Discard stale signals.
        }
        while (true) {
            val result = call()
            POLogger.debug("Attempted to capture the payment.")
            if (!isRetryable(result)) {
                return result
            }
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            val remainingTime = timeout - elapsedTime
            if (remainingTime <= 0) {
                break
            }
            val waitTime = min(backoffIterator.next(), remainingTime)
            select {
                onTimeout(timeMillis = waitTime) {}
                backoffResetSignal.onReceive {
                    backoffIterator = retryStrategy.newIterator()
                    POLogger.debug("Capture polling backoff has been reset.")
                }
            }
        }
        return ProcessOutResult.Failure(
            code = Timeout(),
            message = "Payment confirmation has timed out."
        )
    }

    fun resetBackoff() {
        backoffResetSignal.trySend(Unit)
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
        onFailure = { failure ->
            val retryableCodes = listOf(
                NetworkUnreachable,
                Timeout(),
                Internal()
            )
            retryableCodes.contains(failure.code)
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
