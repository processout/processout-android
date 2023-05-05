# Module ProcessOut Android SDK

## API Examples

### List available native APMs
```
// Coroutine function

val request = POAllGatewayConfigurationsRequest(
    filter = POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS,
    withDisabled = false
)
val result = ProcessOut.instance.gatewayConfigurations.fetch(request)
when (result) {
    is ProcessOutResult.Success -> TODO()
    is ProcessOutResult.Failure -> TODO()
}

// Callback function

val request = POAllGatewayConfigurationsRequest(
    filter = POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS,
    withDisabled = false
)
ProcessOut.instance.gatewayConfigurations.fetch(
    request = request,
    callback = object : ProcessOutCallback<POAllGatewayConfigurations> {
        override fun onSuccess(result: POAllGatewayConfigurations) {
            TODO()
        }

        override fun onFailure(
            message: String,
            code: POFailure.Code,
            invalidFields: List<POFailure.InvalidField>?,
            cause: Exception?
        ) {
            TODO()
        }
    }
)
```
