package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent

/**
 * Delegates events during management of saved payment methods.
 */
interface POSavedPaymentMethodsDelegate {

    /**
     * Delegates events during management of saved payment methods.
     */
    fun onEvent(event: POSavedPaymentMethodsEvent) {}
}
