# Module ProcessOut Android SDK

## Getting Started With 3DS

Some SDK methods may trigger 3DS flow, those methods could be identified by presence of required
parameter `threeDSService: PO3DSService`. For example:
```
ProcessOut.instance.invoices.authorizeInvoice(
    request: POInvoiceAuthorizationRequest,
    threeDSService: PO3DSService,
    callback: (ProcessOutResult<Unit>) -> Unit
)
```

### 3DS2

Most PSPs have their own certified SDKs for 3DS2 in mobile apps but they all have equivalent features.
`PO3DSService` allows to abstract the details of 3DS handling and supply functionality in a consistent way.

We have our own implementation `POTest3DSService` that emulates the normal 3DS authentication flow
but does not actually make any calls to a real Access Control Server (ACS).
It is mainly useful during development in our sandbox testing environment.
Usage example:
```
fun authorizeInvoice(invoiceId: String, cardId: String) {
    ProcessOut.instance.invoices.authorizeInvoice(
        request = POInvoiceAuthorizationRequest(
            invoiceId = invoiceId,
            source = cardId
        ),
        threeDSService = POTest3DSService(activity = this, customTabLauncher = null)
    ) { result ->
        when (result) {
            is ProcessOutResult.Success -> TODO()
            is ProcessOutResult.Failure -> TODO()
        }
    }
}
```

### 3DS Redirect with Custom Chrome Tabs

To handle web based redirects service must implement method:\
`PO3DSService.handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit)`

`PO3DSRedirectCustomTabLauncher` allows to automatically redirect user to provided url and collect the result.
Launcher will open Custom Tab ([overview](https://developer.chrome.com/docs/android/custom-tabs/)) when Chrome
is installed and enabled on the device even if it's not a default browser,
otherwise it will automatically fallback to the WebView.

SDK handles [deep link](https://developer.android.com/training/app-links#deep-links) to return back to your app after authorization
in the following format: `your.application.id://processout/return`\
It is required to provide this deep link as `return_url` when creating invoice.

Integration steps:
```kotlin
// 1) Initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
}

// 2) Pass launcher to your implementation of PO3DSService or POTest3DSService and handle redirect.

override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
    customTabLauncher.launch(redirect) { result ->
        callback(result)
    }
}
```

### 3DS Redirect with WebView (Deprecated)

To handle web based redirects service must implement method:\
`PO3DSService.handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit)`

`PO3DSRedirectWebViewBuilder` allows to create WebView that will automatically redirect user to provided url and collect
the result. WebView must be visible and added to the screen layout.\
Example implementation:
```
private val rootLayout: FrameLayout = activity.findViewById(android.R.id.content)
private val webViewBuilder = PO3DSRedirectWebViewBuilder(activity)
private var webView: WebView? = null

override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
    webView = webViewBuilder.with(redirect) { result ->
        destroyWebView()
        callback(result)
    }.build()
    rootLayout.addView(webView)
}

private fun destroyWebView() {
    webView?.run {
        loadUrl("about:blank")
        rootLayout.removeView(this)
        destroy()
    }.also { webView = null }
}
```
