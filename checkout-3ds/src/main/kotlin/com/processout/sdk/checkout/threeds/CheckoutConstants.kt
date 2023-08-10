package com.processout.sdk.checkout.threeds

internal class CheckoutConstants private constructor() {

    internal object AuthenticationProcessErrorCodes {
        const val E1002_Challenge_cancelled = "challenge_cancelled"
        const val E1003_Challenge_timeout = "challenge_timeout"
    }

    internal object ConnectivityErrorCode {
        const val E2001_connection_failed = "connection_failed"
        const val E2002_connection_timeout = "connection_timeout"
    }
}
