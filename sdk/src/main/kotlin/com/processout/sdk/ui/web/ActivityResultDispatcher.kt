package com.processout.sdk.ui.web

import android.os.Parcelable
import com.processout.sdk.core.ProcessOutActivityResult

internal interface ActivityResultDispatcher<T : Parcelable> {

    fun dispatch(result: ProcessOutActivityResult<T>)
}
