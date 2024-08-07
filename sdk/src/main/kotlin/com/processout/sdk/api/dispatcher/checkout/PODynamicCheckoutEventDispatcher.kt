package com.processout.sdk.api.dispatcher.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.SharedFlow

/** @suppress */
@ProcessOutInternalApi
interface PODynamicCheckoutEventDispatcher {

    val cardTokenizationEvents: SharedFlow<POCardTokenizationEvent>

    val nativeAlternativePaymentEvents: SharedFlow<PONativeAlternativePaymentMethodEvent>
}
