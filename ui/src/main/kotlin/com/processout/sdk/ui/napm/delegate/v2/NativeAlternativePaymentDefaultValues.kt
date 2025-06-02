package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep
import java.util.UUID

internal data class NativeAlternativePaymentDefaultValuesRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val gatewayConfigurationId: String,
    val parameters: List<PONativeAlternativePaymentNextStep.SubmitData.Parameter>
) : POEventDispatcher.Request

internal data class NativeAlternativePaymentDefaultValuesResponse(
    override val uuid: UUID,
    val defaultValues: Map<String, PONativeAlternativePaymentAuthorizationRequest.Parameter>
) : POEventDispatcher.Response

internal fun NativeAlternativePaymentDefaultValuesRequest.toResponse(
    defaultValues: Map<String, PONativeAlternativePaymentAuthorizationRequest.Parameter>
) = NativeAlternativePaymentDefaultValuesResponse(uuid, defaultValues)
