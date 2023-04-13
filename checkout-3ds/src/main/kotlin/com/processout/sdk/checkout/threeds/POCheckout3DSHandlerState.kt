package com.processout.sdk.checkout.threeds

import com.checkout.threeds.standalone.api.ThreeDS2Service
import com.checkout.threeds.standalone.api.Transaction

internal sealed class POCheckout3DSHandlerState {
    object Idle : POCheckout3DSHandlerState()

    data class Fingerprinting(
        val serviceContext: POCheckout3DS2ServiceContext
    ) : POCheckout3DSHandlerState()

    data class Fingerprinted(
        val serviceContext: POCheckout3DS2ServiceContext
    ) : POCheckout3DSHandlerState()

    data class Challenging(
        val serviceContext: POCheckout3DS2ServiceContext
    ) : POCheckout3DSHandlerState()
}

internal data class POCheckout3DS2ServiceContext(
    val threeDS2Service: ThreeDS2Service,
    val transaction: Transaction
)

internal inline fun POCheckout3DSHandlerState.doWhenFingerprinted(
    crossinline block: (serviceContext: POCheckout3DS2ServiceContext) -> Unit
) {
    if (this is POCheckout3DSHandlerState.Fingerprinted) {
        block(serviceContext)
    }
}
