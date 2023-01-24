# Module sdk

## Initialization
```
import com.processout.sdk.api.ProcessOutApi

ProcessOutApi.configure(
    ProcessOutApiConfiguration(
        application = this,
        projectId = "your_project_id"
    )
)
```

## Access initialized singleton instances
```
ProcessOutApi.instance

// Legacy deprecated ProcessOut
ProcessOutApi.legacyInstance
```

## List the available native APMs
```
// Coroutine function

val request = POAllGatewayConfigurationsRequest(
    filter = POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS,
    withDisabled = false
)
val result = ProcessOutApi.instance.gatewayConfigurations.fetch(request)
when (result) {
    is ProcessOutResult.Success -> TODO()
    is ProcessOutResult.Failure -> TODO()
}

// Callback function

val request = POAllGatewayConfigurationsRequest(
    filter = POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS,
    withDisabled = false
)
ProcessOutApi.instance.gatewayConfigurations.fetch(
    request = request,
    callback = object : ProcessOutCallback<POAllGatewayConfigurations> {
        override fun onSuccess(result: POAllGatewayConfigurations) {
            TODO()
        }
        override fun onFailure(e: Exception) {
            TODO()
        }
    }
)
```

## Launch the native APM payment sheet
```
private lateinit var launcher: PONativeAlternativePaymentMethodLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // It is required to initialize launcher in onCreate() method of Activity or Fragment.
    launcher = PONativeAlternativePaymentMethodLauncher.create(from = this) { result ->
        when (result) {
            PONativeAlternativePaymentMethodResult.Success -> TODO()
            PONativeAlternativePaymentMethodResult.Canceled -> TODO()
            is PONativeAlternativePaymentMethodResult.Failure -> TODO()
        }
    }
}

...

launcher.launch(
    PONativeAlternativePaymentMethodConfiguration(
        gatewayConfigurationId = "gateway_configuration_id",
        invoiceId = "invoice_id"
    )
)
```

## Customization of the payment sheet
```
val payButtonStyle = POButtonStyle(
    normal = POButtonStateStyle(
        text = POTextStyle(Color.WHITE, POTypography.actionDefaultMedium),
        border = POBorderStyle(radiusDp = 16, widthDp = 0, color = Color.TRANSPARENT),
        backgroundColor = Color.parseColor("#ffff8800")
    ),
    disabled = POButtonStateStyle(
        text = POTextStyle(Color.GRAY, POTypography.actionDefaultMedium),
        border = POBorderStyle(radiusDp = 16, widthDp = 0, color = Color.TRANSPARENT),
        backgroundColor = Color.LTGRAY
    ),
    highlightedBackgroundColor = Color.parseColor("#ffffbb33"),
    progressIndicatorColor = Color.WHITE
)

launcher.launch(
    PONativeAlternativePaymentMethodConfiguration(
        gatewayConfigurationId = "gateway_configuration_id",
        invoiceId = "invoice_id",
        style = PONativeAlternativePaymentMethodConfiguration.Style(
            submitButton = payButtonStyle
        ),
        uiConfiguration = PONativeAlternativePaymentMethodConfiguration.UiConfiguration(
            title = "Payment details",
            submitButtonText = "Submit",
            successMessage = "Payment confirmed.\nThank you!"
        ),
        options = PONativeAlternativePaymentMethodConfiguration.Options(
            isBottomSheetCancelable = true
        )
    )
)
```
