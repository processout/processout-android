package com.processout.sdk.ui.savedpaymentmethods.delegate

/**
 * Delegates events during management of saved payment methods.
 */
interface POSavedPaymentMethodsDelegate {

    /**
     * Delegates events during management of saved payment methods.
     */
    fun onEvent(event: POSavedPaymentMethodsEvent) {}
}
