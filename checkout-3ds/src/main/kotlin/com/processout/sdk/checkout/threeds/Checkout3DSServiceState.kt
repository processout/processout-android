package com.processout.sdk.checkout.threeds

import com.checkout.threeds.standalone.api.ThreeDS2Service
import com.checkout.threeds.standalone.api.Transaction

internal sealed class Checkout3DSServiceState {
    data object Idle : Checkout3DSServiceState()

    data class Fingerprinting(
        val serviceContext: Checkout3DSServiceContext
    ) : Checkout3DSServiceState()

    data class Fingerprinted(
        val serviceContext: Checkout3DSServiceContext
    ) : Checkout3DSServiceState()

    data class Challenging(
        val serviceContext: Checkout3DSServiceContext
    ) : Checkout3DSServiceState()
}

internal data class Checkout3DSServiceContext(
    val threeDS2Service: ThreeDS2Service,
    val transaction: Transaction
)

internal inline fun Checkout3DSServiceState.doWhenFingerprinted(
    crossinline block: (serviceContext: Checkout3DSServiceContext) -> Unit
) {
    if (this is Checkout3DSServiceState.Fingerprinted) {
        block(serviceContext)
    }
}
