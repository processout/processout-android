# Module ProcessOut Android SDK - Checkout 3DS

## Overview

Framework wraps Checkout SDK to make it easy to use with ProcessOut when making requests that may trigger 3DS2.

## Integration

SDK handles [deep link](https://developer.android.com/training/app-links#deep-links) to return back to your app after authorization
in the following format: `your.application.id://processout/return`\
It is required to provide this deep link as `return_url` when creating invoice.

### Implement 3DS service delegate

```kotlin
// 1) Initialize PO3DSRedirectCustomTabLauncher in onCreate() method of Activity or Fragment.

private lateinit var customTabLauncher: PO3DSRedirectCustomTabLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    customTabLauncher = PO3DSRedirectCustomTabLauncher.create(from = this)
}

// 2) Implement POCheckout3DSServiceDelegate and pass launcher.

class Checkout3DSServiceDelegate(
    private val activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher
) : POCheckout3DSServiceDelegate {

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
        customTabLauncher.launch(redirect) { result ->
            callback(result)
        }
    }
}
```

### Create 3DS service

```kotlin
val threeDSService = POCheckout3DSService.Builder(
    activity = this,
    delegate = Checkout3DSServiceDelegate(activity = this, customTabLauncher)
)   // Optional parameter, by default Environment.PRODUCTION
    .with(environment = Environment.SANDBOX)
    .build()
```
