package com.processout.sdk.core

import android.os.Parcelable
import com.processout.sdk.core.ProcessOutResult.Failure
import com.processout.sdk.core.ProcessOutResult.Success
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

fun <T : Parcelable> ProcessOutResult<T>.toActivityResult(): ProcessOutActivityResult<T> =
    when (this) {
        is Success -> ProcessOutActivityResult.Success(value)
        is Failure -> ProcessOutActivityResult.Failure(code, message)
    }

@Parcelize
data object POUnit : Parcelable
