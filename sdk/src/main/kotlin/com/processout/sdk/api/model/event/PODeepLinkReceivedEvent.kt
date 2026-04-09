package com.processout.sdk.api.model.event

import android.net.Uri
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class PODeepLinkReceivedEvent(
    val uri: Uri
)
