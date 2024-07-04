package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val paymentMethods: List<PODynamicCheckoutPaymentMethod>
)
