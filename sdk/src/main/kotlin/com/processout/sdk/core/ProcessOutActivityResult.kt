package com.processout.sdk.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ProcessOutActivityResult<out T : Parcelable> : Parcelable {
    @Parcelize
    data class Success<out T : Parcelable>(val value: T) : ProcessOutActivityResult<T>()

    @Parcelize
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null
    ) : ProcessOutActivityResult<Nothing>()
}
