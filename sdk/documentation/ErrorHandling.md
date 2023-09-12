# Module ProcessOut Android SDK

## Error Handling

Occurred errors are provided
as [ProcessOutResult.Failure](https://processout.github.io/processout-android/sdk/com.processout.sdk.core/-process-out-result/-failure/index.html)
with [POFailure](https://processout.github.io/processout-android/sdk/com.processout.sdk.core/-p-o-failure/index.html)
error code and details.

```kotlin
when (result) {
    is ProcessOutResult.Success -> TODO()
    is ProcessOutResult.Failure -> {
        // Raw error code value is the same on Android and iOS for the same error code.
        val rawErrorCodeValue: String = result.code.rawValue

        // Handle specific failure types.
        when (val code = result.code) {
            is POFailure.Code.Authentication -> code.authenticationCode // TODO()
            is POFailure.Code.Validation -> code.validationCode // TODO()
            is POFailure.Code.NotFound -> code.notFoundCode // TODO()
            is POFailure.Code.Timeout ->
                when (code.timeoutCode) {
                    POFailure.TimeoutCode.mobile -> TODO()
                    POFailure.TimeoutCode.gateway -> TODO()
                }
            POFailure.Code.NetworkUnreachable -> TODO()
            POFailure.Code.Cancelled -> TODO()
            is POFailure.Code.Generic -> code.genericCode // TODO()
            is POFailure.Code.Internal -> code.internalCode // TODO()
            is POFailure.Code.Unknown -> code.rawValue // TODO()
        }
    }
}
```
