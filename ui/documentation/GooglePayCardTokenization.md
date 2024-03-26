# Module ProcessOut Android SDK - UI

## Google Pay Card Tokenization (since 4.16.0)

### Overview

ProcessOut integration with Google Pay API for card tokenization.
Please also check our [documentation](https://docs.processout.com/docs/using-google-pay)
and [Google's overview](https://developers.google.com/pay/api/android/overview)
for initial configuration.

### Launch Google Pay Payment Sheet

```kotlin
// 1) It is required to initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var launcher: POGooglePayCardTokenizationLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = POGooglePayCardTokenizationLauncher.create(
        from = this,
        walletOptions = WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
    ) { result ->
        result.onSuccess { data ->
            // Get a ProcessOut card token created from a Google Pay token.
            // It can be used to authorize an invoice or assign a customer token.
            data.card.id
        }.onFailure { TODO() }
    }
}

// 2) Check API readiness and setup a Google Pay button to launch the payment sheet.
//    Note that GooglePayConfiguration is only an example and does not exist.
//    You need to provide your own configuration and requests by following Google's tutorial.

lifecycleScope.launch {
    if (!launcher.isReadyToPay(GooglePayConfiguration.isReadyToPayRequest())) {
        return@launch
    }
    with(binding.googlePayButton) {
        val options = ButtonOptions.newBuilder()
            .setAllowedPaymentMethods(GooglePayConfiguration.allowedPaymentMethods.toString())
            .build()
        initialize(options)
        setOnClickListener {
            val paymentDataRequestJson = GooglePayConfiguration.getPaymentDataRequest(priceCents = 100L)
            launcher.launch(paymentDataRequestJson)
        }
    }
}
```
