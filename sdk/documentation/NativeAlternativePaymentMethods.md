# Module ProcessOut Android SDK

## Native Alternative Payment Methods

### Launch native APM payment sheet

```kotlin
// 1) It is required to initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var launcher: PONativeAlternativePaymentMethodLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = PONativeAlternativePaymentMethodLauncher.create(from = this) { result ->
        when (result) {
            PONativeAlternativePaymentMethodResult.Success -> TODO()
            is PONativeAlternativePaymentMethodResult.Failure -> TODO()
        }
    }
}

// 2) Launch the activity.

launcher.launch(
    PONativeAlternativePaymentMethodConfiguration(
        gatewayConfigurationId = "gway_conf_",
        invoiceId = "iv_"
    )
)
```

### Customization of the payment sheet

```kotlin
val payButtonStyle = POButtonStyle(
    normal = POButtonStateStyle(
        text = POTextStyle(Color.WHITE, POTypography.actionDefaultMedium),
        border = POBorderStyle(radiusDp = 16, widthDp = 0, color = Color.TRANSPARENT),
        backgroundColor = Color.parseColor("#ffff8800"),
        elevationDp = 2,
        paddingDp = POButtonStateStyle.DEFAULT_PADDING
    ),
    disabled = POButtonStateStyle(
        text = POTextStyle(Color.GRAY, POTypography.actionDefaultMedium),
        border = POBorderStyle(radiusDp = 16, widthDp = 0, color = Color.TRANSPARENT),
        backgroundColor = Color.LTGRAY,
        elevationDp = 0,
        paddingDp = POButtonStateStyle.DEFAULT_PADDING
    ),
    highlighted = POButtonHighlightedStyle(
        textColor = Color.WHITE,
        borderColor = Color.TRANSPARENT,
        backgroundColor = Color.parseColor("#ffffbb33")
    ),
    progressIndicatorColor = Color.WHITE
)

launcher.launch(
    PONativeAlternativePaymentMethodConfiguration(
        gatewayConfigurationId = "gway_conf_",
        invoiceId = "iv_",
        style = Style(
            primaryButton = payButtonStyle
        ),
        options = Options(
            title = "Payment details",
            primaryActionText = "Submit",
            secondaryAction = SecondaryAction.Cancel("Go Back"),
            cancellation = Cancellation(
                dragDown = true,
                touchOutside = false,
                backPressed = false
            ),
            successMessage = "Payment confirmed.\nThank you!",
            skipSuccessScreen = true, // Only applies when 'waitsPaymentConfirmation = true'
            waitsPaymentConfirmation = true,
            paymentConfirmationTimeoutSeconds = Options.MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS,
            paymentConfirmationSecondaryAction = SecondaryAction.Cancel(
                text = "Cancel",
                disabledForSeconds = 30
            )
        )
    )
)
```

### Payment sheet error handling

```kotlin
when (result) {
    PONativeAlternativePaymentMethodResult.Success -> TODO()
    is PONativeAlternativePaymentMethodResult.Failure -> {
        // Raw error code value is the same on Android and iOS for the same error code.
        val rawErrorCodeValue: String = result.code.rawValue

        // Handle specific failure types.
        when (val code = result.code) {
            is POFailure.Code.Authentication -> code.authenticationCode // TODO()
            is POFailure.Code.Validation -> code.validationCode // TODO()
            is POFailure.Code.NotFound -> code.notFoundCode // TODO()
            is POFailure.Code.Timeout -> code.timeoutCode // TODO()
            POFailure.Code.NetworkUnreachable -> TODO()
            POFailure.Code.Cancelled -> TODO()
            is POFailure.Code.Generic -> code.genericCode // TODO()
            is POFailure.Code.Internal -> code.internalCode // TODO()
            is POFailure.Code.Unknown -> code.rawValue // TODO()
        }
    }
}
```

### Subscribe to payment sheet lifecycle events

```kotlin
viewModelScope.launch {
    ProcessOut.instance
        .nativeAlternativePaymentMethodEventDispatcher
        .events
        .collect { event ->
            when (event) {
                PONativeAlternativePaymentMethodEvent.WillStart -> TODO()
                PONativeAlternativePaymentMethodEvent.DidStart -> TODO()
                PONativeAlternativePaymentMethodEvent.ParametersChanged -> TODO()
                PONativeAlternativePaymentMethodEvent.WillSubmitParameters -> TODO()
                is PONativeAlternativePaymentMethodEvent.DidSubmitParameters -> TODO()
                is PONativeAlternativePaymentMethodEvent.DidFailToSubmitParameters -> TODO()
                is PONativeAlternativePaymentMethodEvent.WillWaitForCaptureConfirmation -> TODO()
                PONativeAlternativePaymentMethodEvent.DidCompletePayment -> TODO()
                is PONativeAlternativePaymentMethodEvent.DidFail -> TODO()
            }
        }
}
```

### Provide default values for payment sheet input fields

```kotlin
viewModelScope.launch {
    with(ProcessOut.instance.nativeAlternativePaymentMethodEventDispatcher) {
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
