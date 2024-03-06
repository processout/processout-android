package com.processout.sdk.ui.web

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal enum class ActivityResultApi : Parcelable {
    Android,
    Dispatcher
}
