# Module ProcessOut Android SDK - UI

## Native Alternative Payment Methods (Compose) (since 4.19.0)

### Launch Payment Sheet

```kotlin
// 1) Initialize the launcher in the onCreate() method of Activity or Fragment.

import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentLauncher

private lateinit var launcher: PONativeAlternativePaymentLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = PONativeAlternativePaymentLauncher.create(from = this) { result ->
        result.onSuccess {
            // Handle success.
        }.onFailure { failure ->
            // Handle failure.
        }
    }
}

// 2) Launch the activity.

launcher.launch(
    PONativeAlternativePaymentConfiguration(
        invoiceId = "iv_",
        gatewayConfigurationId = "gway_conf_"
    )
)
```

### Configuration & Customization

```kotlin
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.*

launcher.launch(
    PONativeAlternativePaymentConfiguration(
        invoiceId = "iv_",
        gatewayConfigurationId = "gway_conf_",
        options = Options(
            title = "Payment Details",
            primaryActionText = "Submit",
            secondaryAction = SecondaryAction.Cancel(text = "Cancel"),
            cancellation = CancellationConfiguration(
                backPressed = false,
                dragDown = true,
                touchOutside = false
            ),
            paymentConfirmation = PaymentConfirmationConfiguration(
                waitsConfirmation = true,
                timeoutSeconds = PaymentConfirmationConfiguration.MAX_TIMEOUT_SECONDS,
                showProgressIndicatorAfterSeconds = 10,
                secondaryAction = SecondaryAction.Cancel(
                    text = "Cancel",
                    disabledForSeconds = 10,
                    confirmation = POActionConfirmationConfiguration(
                        title = "Cancel payment?",
                        message = "Your payment will be cancelled.",
                        confirmActionText = "Cancel payment",
                        dismissActionText = "Not now"
                    )
                )
            ),
            inlineSingleSelectValuesLimit = 5,
            skipSuccessScreen = true,
            successMessage = null
        ),
        style = Style(
            // Customize the look and feel.
        )
    )
)
```

### Error Handling

```kotlin
result.onFailure { failure ->
    // Raw error code value is the same on Android and iOS for the same error code.
    val rawErrorCodeValue: String = failure.code.rawValue

    // Handle specific failure types.
    when (val code = failure.code) {
        Cancelled -> TODO()
        NetworkUnreachable -> TODO()
        is Timeout -> code.timeoutCode // TODO()
        is Generic -> code.genericCode // TODO()
        is Authentication -> code.authenticationCode // TODO()
        is Validation -> code.validationCode // TODO()
        is NotFound -> code.notFoundCode // TODO()
        is Internal -> code.internalCode // TODO()
        is Unknown -> code.rawValue // TODO()
    }
}
```

### Lifecycle Events

```kotlin
viewModelScope.launch {
    ProcessOut.instance.dispatchers.nativeAlternativePaymentMethod
        .events.collect { event ->
            when (event) {
                WillStart -> TODO()
                DidStart -> TODO()
                ParametersChanged -> TODO()
                WillSubmitParameters -> TODO()
                is DidSubmitParameters -> TODO()
                is DidFailToSubmitParameters -> TODO()
                is WillWaitForCaptureConfirmation -> TODO()
                DidRequestCancelConfirmation -> TODO()
                DidCompletePayment -> TODO()
                is DidFail -> TODO()
            }
        }
}
```

### Provide Default Values

```kotlin
viewModelScope.launch {
    with(ProcessOut.instance.dispatchers.nativeAlternativePaymentMethod) {
        // Subscribe for request to provide default values.
        defaultValuesRequest.collect { request ->
            // Default values should be provided as Map<String, String>
            // where key is PONativeAlternativePaymentMethodParameter.key
            // and value is a custom default value.
            val defaultValues = mutableMapOf<String, String>()

            // Populate default values map based on request parameters.
            // It's not mandatory to provide defaults for all parameters.
            request.parameters.find {
                it.type() == ParameterType.PHONE
            }?.also {
                defaultValues[it.key] = "+111122223333"
            }

            // Provide response which must be constructed from request with default values payload.
            // Note that once you've subscribed to 'defaultValuesRequest'
            // it's required to send response back, otherwise the payment flow will not proceed.
            // If there is no default values to provide it's still required
            // to call this method with 'emptyMap()'.
            provideDefaultValues(request.toResponse(defaultValues))
        }
    }
}
```
