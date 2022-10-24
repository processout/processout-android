package com.processout.sdk.core

interface ProcessOutCallback<in T : Any> {
    fun onSuccess(result: T)
    fun onFailure(e: Exception)
}
