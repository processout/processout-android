# Module ProcessOut Android SDK - Checkout 3DS

## Overview

Framework wraps Checkout SDK to make it easy to use with ProcessOut when making requests that may trigger 3DS2.

## Integration

### Implement 3DS service delegate

```kotlin
class Checkout3DSServiceDelegate(
    private val activity: Activity
) : POCheckout3DSServiceDelegate {

    // In this example we're handling redirect with the WebView
    // that will be added to the root layout of provided activity.
    // You can pass your custom layout and/or additional customization properties into constructor.
    private val rootLayout: FrameLayout = activity.findViewById(android.R.id.content)
    private val webViewBuilder = PO3DSRedirectWebViewBuilder(activity)
    private var webView: WebView? = null

    override fun configuration(parameters: ConfigParameters): ThreeDS2ServiceConfiguration {
        return ThreeDS2ServiceConfiguration(
            context = activity,
            configParameters = parameters,
            // Optional properties.
            locale = Locale.UK,
            uiCustomization = UICustomization(),
            appUri = Uri.parse("https://my-app-url.com"),
            challengeTimeout = 300
        )
    }

    override fun shouldContinue(warnings: Set<Warning>, callback: (Boolean) -> Unit) {
        // Default implementation ignores all warnings.
        // As an example we can define to continue the flow
        // only if there is no warnings or all warnings have low severity.
        callback(warnings.all { it.severity == Severity.LOW })
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        // Create WebView with `PO3DSRedirectWebViewBuilder`.
        webView = webViewBuilder.with(redirect) { result ->
            destroyWebView()
            callback(result)
        }.build()
        // Note that some redirects can be handled without showing any actual UI to user.
        // We can check `redirect.isHeadlessModeAllowed` to decide if we need to add WebView to layout.
        // WebView will handle redirect in headless mode without UI and user input.
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
}
```

### Create 3DS service

```kotlin
val threeDSService = POCheckout3DSService.Builder(
    activity = this,
    delegate = Checkout3DSServiceDelegate(activity = this)
)   // Optional parameter, by default Environment.PRODUCTION
    .with(environment = Environment.SANDBOX)
    .build()
```
