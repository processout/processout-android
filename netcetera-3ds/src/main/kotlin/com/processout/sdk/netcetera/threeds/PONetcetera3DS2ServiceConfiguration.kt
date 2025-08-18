package com.processout.sdk.netcetera.threeds

import com.netcetera.threeds.sdk.api.transaction.Transaction.BridgingMessageExtensionVersion
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType
import com.processout.sdk.netcetera.threeds.PONetcetera3DS2ServiceConfiguration.AuthenticationMode.COMPATIBILITY
import java.util.Locale

data class PONetcetera3DS2ServiceConfiguration(
    val authenticationMode: AuthenticationMode = COMPATIBILITY,
    val challengeTimeoutSeconds: Int = 5 * 60,
    val locale: Locale? = null,
    val returnUrl: String? = null,
    val bridgingExtensionVersion: BridgingMessageExtensionVersion? = null,
    val uiCustomizations: Map<UiCustomizationType, UiCustomization>? = null
) {

    enum class AuthenticationMode {
        FULL,
        COMPATIBILITY
    }
}
