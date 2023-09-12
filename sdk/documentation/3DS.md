# Module ProcessOut Android SDK

## Getting Started With 3DS

Some SDK methods may trigger 3DS flow, those methods could be identified by presence of required
parameter `threeDSService: PO3DSService`. For example:

```kotlin
fun authorizeInvoice(
    request: POInvoiceAuthorizationRequest,
    threeDSService: PO3DSService
)
```

### 3DS2

Most PSPs have their own certified SDKs for 3DS2 in mobile apps but they all have equivalent features.
`PO3DSService` allows to abstract the details of 3DS handling and supply functionality in a consistent way.

We have our own implementation `POTest3DSService` that emulates the normal 3DS authentication flow
but does not actually make any calls to a real Access Control Server (ACS).
It is mainly useful during development in our sandbox testing environment.
Usage example:

```kotlin
fun authorizeInvoice(invoiceId: String, cardId: String) {
    ProcessOut.instance.invoices.authorizeInvoice(
        request = POInvoiceAuthorizationRequest(
            invoiceId = invoiceId,
            source = cardId
        ),
        threeDSService = POTest3DSService(activity = this, customTabLauncher = null)
    )
}

// Subscribe to collect result in coroutines scope before calling method.
ProcessOut.instance.invoices.authorizeInvoiceResult
    .collect { result ->
        // handle result
    }
```

### 3DS Redirect with Custom Chrome Tabs (since 4.7.0)

To handle web based redirects service must implement method:\
`PO3DSService.handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit)`

`PO3DSRedirectCustomTabLauncher` allows to automatically redirect user to provided URL and collect the result.
Launcher will open Custom Tab ([overview](https://developer.chrome.com/docs/android/custom-tabs/)) when Chrome
is installed and enabled on the device even if it's not a default browser,
otherwise it will automatically fallback to the WebView.

SDK handles [deep link](https://developer.android.com/training/app-links#deep-links) to return back to your app after
authorization in the following format: `your.application.id://processout/return`\
It is required to provide this deep link on the backend as `return_url` when creating invoice and as `invoice_return_url`
when creating token.

Integration steps:

```kotlin
// 1) It is required to initialize PO3DSRedirectCustomTabLauncher in onCreate() method of Activity or Fragment.

private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
}

// 2) Pass launcher to implementation of PO3DSService or POTest3DSService and handle redirect.

override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
    customTabLauncher.launch(
        redirect = redirect,
        returnUrl = "your.application.id://processout/return",
        callback = callback
    )
}
```
