package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodDefaultValuesResponse
import kotlinx.coroutines.flow.SharedFlow

// TODO: Move it to '.dispatcher.napm' package before next major release.
// TODO: Rename to 'PONativeAlternativePaymentEventDispatcher'.

/**
 * Dispatcher that allows to handle events during native alternative payments.
 */
interface PONativeAlternativePaymentMethodEventDispatcher {

    /**
     * Allows to subscribe for native alternative payment lifecycle events.
     */
    val events: SharedFlow<PONativeAlternativePaymentMethodEvent>

    /**
     * Allows to subscribe for request to provide default values.
     * Once you've subscribed it's required to call [provideDefaultValues]
     * for each request to proceed with the payment flow.
     */
    val defaultValuesRequest: SharedFlow<PONativeAlternativePaymentMethodDefaultValuesRequest>

    /**
     * Allows to provide default values response which must be constructed from request
     * that has been collected by subscribing to [defaultValuesRequest].
     *
     * ```
     * viewModelScope.launch {
     *     with(ProcessOut.instance.dispatchers.nativeAlternativePaymentMethod) {
     *         // Subscribe for request to provide default values.
     *         defaultValuesRequest.collect { request ->
     *             // Default values should be provided as Map<String, String>
     *             // where key is a [PONativeAlternativePaymentMethodParameter.key]
     *             // and value is a custom default value.
     *             val defaultValues = mutableMapOf<String, String>()
     *
     *             // Populate default values map based on request parameters.
     *             // It's not mandatory to provide defaults for all parameters.
     *             request.parameters.find {
     *                 it.type() == ParameterType.PHONE
     *             }?.also {
     *                 defaultValues[it.key] = "+111122223333"
     *             }
     *
     *             // Provide response which must be constructed from request with default values payload.
     *             // Note that once you've subscribed to 'defaultValuesRequest'
     *             // it's required to send response back, otherwise the payment flow will not proceed.
     *             // If there is no default values to provide it's still required
     *             // to call this method with 'emptyMap()'.
     *             provideDefaultValues(request.toResponse(defaultValues))
     *         }
     *     }
     * }
     * ```
     */
    suspend fun provideDefaultValues(response: PONativeAlternativePaymentMethodDefaultValuesResponse)
}
