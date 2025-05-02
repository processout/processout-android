package com.processout.sdk.ui.checkout.dispatcher

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.ui.savedpaymentmethods.POSavedPaymentMethodsConfiguration
import java.util.UUID

internal data class DynamicCheckoutSavedPaymentMethodsConfigurationRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val configuration: POSavedPaymentMethodsConfiguration
) : POEventDispatcher.Request

internal data class DynamicCheckoutSavedPaymentMethodsConfigurationResponse(
    override val uuid: UUID,
    val configuration: POSavedPaymentMethodsConfiguration
) : POEventDispatcher.Response

internal fun DynamicCheckoutSavedPaymentMethodsConfigurationRequest.toResponse(
    configuration: POSavedPaymentMethodsConfiguration
) = DynamicCheckoutSavedPaymentMethodsConfigurationResponse(uuid, configuration)
