@file:Suppress("unused")

package com.processout.sdk.netcetera.threeds

import com.netcetera.threeds.sdk.api.transaction.Transaction.BridgingMessageExtensionVersion
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType
import com.processout.sdk.netcetera.threeds.PONetcetera3DS2ServiceConfiguration.AuthenticationMode.COMPATIBILITY
import java.util.Locale

/**
 * Specifies [PONetcetera3DS2Service] configuration.
 *
 * @param[authenticationMode] Authentication mode determines which device information parameters are included in the authentication request.
 * The default value is [AuthenticationMode.COMPATIBILITY].
 * @param[challengeTimeoutSeconds] Timeout (in seconds) to complete the challenge. The minimum and default value is _5 minutes_.
 * @param[locale] Locale of the app's user interface.
 * @param[returnUrl] Return URL for out-of-band (OOB) challenge flow.
 * @param[bridgingExtensionVersion] The Bridging Message Extension describes how existing EMV® 3-D Secure v2.1 and v2.2 components
 * can provide or consume additional data related to the EMV® 3-D Secure Protocol and Core Functions Specification v2.3.1.
 * The 3DS SDK will process the Bridging Message Extension, and if the required elements are present it will enable
 * the OOB Automatic Switching Feature for OOB v2.2 challenges and masking of the challenge input for TEXT v2.2 challenges.
 * @param[uiCustomizations] UI style customizations.
 */
data class PONetcetera3DS2ServiceConfiguration(
    val authenticationMode: AuthenticationMode = COMPATIBILITY,
    val challengeTimeoutSeconds: Int = 5 * 60,
    val locale: Locale? = null,
    val returnUrl: String? = null,
    val bridgingExtensionVersion: BridgingMessageExtensionVersion? = null,
    val uiCustomizations: Map<UiCustomizationType, UiCustomization>? = null
) {

    /**
     * Authentication mode determines which device information parameters are included in the authentication request.
     */
    enum class AuthenticationMode {
        /**
         * Collects all available parameters without restrictions.
         */
        FULL,

        /**
         * Restricts certain parameters to ensure the authentication request is compliant with a wider range of payment providers.
         */
        COMPATIBILITY
    }
}
