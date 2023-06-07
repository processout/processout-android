package com.processout.sdk.api.model.threeds

import java.net.URL

/**
 * @param url Redirect URL.
 * @param timeoutSeconds Optional timeout interval in seconds.
 * @param isHeadlessModeAllowed Boolean value that indicates whether a given URL can be handled in headless mode, meaning without showing any UI for the user.
 */
data class PO3DSRedirect(
    val url: URL,
    val timeoutSeconds: Int? = null,
    @Deprecated("Always 'false'.")
    val isHeadlessModeAllowed: Boolean = false
)
