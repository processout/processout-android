package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/**
 * Delegates events during management of saved payment methods.
 */
/** @suppress */
@ProcessOutInternalApi
interface POSavedPaymentMethodsDelegate {

    /**
     * Delegates events during management of saved payment methods.
     */
    fun onEvent(event: POSavedPaymentMethodsEvent) {}
}
