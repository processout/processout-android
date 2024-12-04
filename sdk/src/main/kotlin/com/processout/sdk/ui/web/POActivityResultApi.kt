package com.processout.sdk.ui.web

import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
enum class POActivityResultApi : Parcelable {
    Android,
    Dispatcher
}
