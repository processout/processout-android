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
        threeDSService = POTest3DSService(activity = this)
    ) { result ->
        when (result) {
            is ProcessOutResult.Success -> TODO()
            is ProcessOutResult.Failure -> TODO()
        }
    }
}
```

### 3DS Redirect

To handle web based redirects service must implement method:\
`PO3DSService.handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit)`

`PO3DSRedirectWebViewBuilder` allows to create WebView that will automatically redirect user to provided url and collect
the result. Note that some redirects can be handled silently to user. Check `isHeadlessModeAllowed` in `PO3DSRedirect`,
if value of this property is `true` then redirect can be handled without showing any additional UI to user,
otherwise WebView must be visible and added to the screen layout.
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
    if (redirect.isHeadlessModeAllowed.not()) {
        rootLayout.addView(webView)
    }
}

private fun destroyWebView() {
    webView?.run {
        loadUrl("about:blank")
        rootLayout.removeView(this)
        destroy()
    }.also { webView = null }
}
```
