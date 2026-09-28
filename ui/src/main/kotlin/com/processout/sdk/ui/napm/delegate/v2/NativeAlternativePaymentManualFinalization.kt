package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAvailableAction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

internal data class NativeAlternativePaymentManualFinalizationRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val paymentState: PONativeAlternativePaymentState,
    val availableActions: List<PONativeAlternativePaymentAvailableAction>
) : POEventDispatcher.Request

internal data class NativeAlternativePaymentManualFinalizationResponse(
    override val uuid: UUID,
    val result: ProcessOutResult<Unit>
) : POEventDispatcher.Response

internal fun NativeAlternativePaymentManualFinalizationRequest.toResponse(
    result: ProcessOutResult<Unit>
) = NativeAlternativePaymentManualFinalizationResponse(uuid, result)
