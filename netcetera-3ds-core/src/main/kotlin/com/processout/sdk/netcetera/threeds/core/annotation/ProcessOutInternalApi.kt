package com.processout.sdk.netcetera.threeds.core.annotation

/** @suppress */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Internal ProcessOut API. Opt-in only for testing purposes."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR
)
annotation class ProcessOutInternalApi
