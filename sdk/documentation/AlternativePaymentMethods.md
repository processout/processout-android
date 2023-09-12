# Module ProcessOut Android SDK

## Alternative Payment Methods

`POAlternativePaymentMethodCustomTabLauncher` allows to handle Alternative Payment Methods by provided request parameters
or URL and collect the result.
Launcher will open Custom Tab ([overview](https://developer.chrome.com/docs/android/custom-tabs/)) when Chrome
is installed and enabled on the device even if it's not a default browser,
otherwise it will automatically fallback to the WebView.

SDK handles [deep link](https://developer.android.com/training/app-links#deep-links) to return back to your app after
authorization in the following format: `your.application.id://processout/return`\
It is required to provide this deep link on the backend as `return_url` when creating invoice and as `invoice_return_url`
when creating token.

Integration steps:

```kotlin
// 1) It is required to initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var customTabLauncher: POAlternativePaymentMethodCustomTabLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(from = this) { result ->
        when (result) {
            is ProcessOutResult.Success -> TODO()
            is ProcessOutResult.Failure -> TODO()
        }
    }
}

// 2) Launch the activity.

// Launch activity with URL created from request.
customTabLauncher.launch(
    request = POAlternativePaymentMethodRequest(
        invoiceId = "iv_",
        gatewayConfigurationId = "gway_conf_",
        customerId = "cust_",
        tokenId = "tok_"
    ),
    returnUrl = "your.application.id://processout/return"
)

// Launch activity with custom URL.
customTabLauncher.launch(
    uri = Uri.parse("https://your_custom_url"),
    returnUrl = "your.application.id://processout/return"
)
```
