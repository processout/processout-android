package com.processout.sdk.netcetera.threeds

import android.content.Context
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.processout.sdk.netcetera.threeds.Netcetera3DS2ServiceState.Fingerprinted

internal sealed interface Netcetera3DS2ServiceState {

    data object Idle : Netcetera3DS2ServiceState

    data object AuthenticationRequest : Netcetera3DS2ServiceState

    data class Fingerprinting(
        val serviceContext: Netcetera3DS2ServiceContext
    ) : Netcetera3DS2ServiceState

    data class Fingerprinted(
        val serviceContext: Netcetera3DS2ServiceContext
    ) : Netcetera3DS2ServiceState

    data class Challenging(
        val serviceContext: Netcetera3DS2ServiceContext
    ) : Netcetera3DS2ServiceState
}

internal data class Netcetera3DS2ServiceContext(
    val applicationContext: Context,
    val threeDS2Service: ThreeDS2Service,
    val transaction: Transaction
)

internal inline fun Netcetera3DS2ServiceState.whenFingerprinted(
    crossinline block: (serviceContext: Netcetera3DS2ServiceContext) -> Unit
) {
    if (this is Fingerprinted) {
        block(serviceContext)
    }
}
