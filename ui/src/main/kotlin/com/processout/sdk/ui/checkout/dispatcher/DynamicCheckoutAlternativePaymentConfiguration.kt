package com.processout.sdk.ui.checkout.dispatcher

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration
import java.util.UUID

internal data class DynamicCheckoutAlternativePaymentConfigurationRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
    val configuration: PODynamicCheckoutConfiguration.AlternativePaymentConfiguration
) : POEventDispatcher.Request

internal data class DynamicCheckoutAlternativePaymentConfigurationResponse(
    override val uuid: UUID,
    val paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
    val configuration: PODynamicCheckoutConfiguration.AlternativePaymentConfiguration
) : POEventDispatcher.Response

internal fun DynamicCheckoutAlternativePaymentConfigurationRequest.toResponse(
    configuration: PODynamicCheckoutConfiguration.AlternativePaymentConfiguration
) = DynamicCheckoutAlternativePaymentConfigurationResponse(uuid, paymentMethod, configuration)
