package com.processout.sdk.core.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Internal ProcessOut API. Opt-in only for testing purposes."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ProcessOutInternalApi
