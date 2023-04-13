package com.processout.sdk.api.model.threeds

import java.net.URL

/**
 * @param url Redirect URL.
 * @param isHeadlessModeAllowed Boolean value that indicates whether a given URL can be handled in headless mode, meaning without showing any UI for the user.
 * @param timeoutSeconds Optional timeout interval in seconds.
 */
data class PO3DSRedirect(
    val url: URL,
    val isHeadlessModeAllowed: Boolean,
    val timeoutSeconds: Int? = null
)
