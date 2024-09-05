# Module ProcessOut Android SDK - UI

## Card Tokenization (since 4.15.0)

### Launch Card Tokenization Bottom Sheet

```kotlin
// 1) It is required to initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var launcher: POCardTokenizationLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = POCardTokenizationLauncher.create(from = this) { result ->
        result.onSuccess { card ->
            TODO()
        }.onFailure { TODO() }
    }
}

// 2) Launch the activity.

launcher.launch(POCardTokenizationConfiguration())
```

### Configuration

```kotlin
POCardTokenizationConfiguration(
    title = "Add New Card",
    isCardholderNameFieldVisible = true,
    billingAddress = POCardTokenizationConfiguration.BillingAddressConfiguration(
        // Configure how to collect the billing address.
    ),
    primaryActionText = "Submit",
    secondaryActionText = "Cancel",
    cancellation = POCancellationConfiguration(
        secondaryAction = true,
        backPressed = false,
        dragDown = true,
        touchOutside = false
    ),
    metadata = null, // Metadata related to the card.
    style = POCardTokenizationConfiguration.Style(
        // Customize the look and feel.
    )
)
```

### Error Handling

```kotlin
viewModelScope.launch {
    with(ProcessOut.instance.dispatchers.cardTokenization) {
        shouldContinueRequest.collect { request ->
            // Inspect the failure to decide whether the flow should continue or complete.
            val shouldContinue = when (val code = request.failure.code) {
                is Generic -> when (code.genericCode) {
                    requestInvalidCard,
                    cardInvalid -> false
                    else -> true
                }
                else -> false
            }
            // Notify by sending the response which must be constructed from request.
            // Note that once you've subscribed to 'shouldContinueRequest'
            // it's required to send response back otherwise the card tokenization flow will not proceed.
            shouldContinue(request.toResponse(shouldContinue = shouldContinue))
        }
    }
}
```

### Lifecycle Events

```kotlin
viewModelScope.launch {
    ProcessOut.instance.dispatchers.cardTokenization
        .events.collect { event ->
            when (event) {
                WillStart -> TODO()
                DidStart -> TODO()
                ParametersChanged -> TODO()
                WillTokenize -> TODO()
                is DidTokenize -> event.card // TODO()
                DidComplete -> TODO()
            }
        }
}
```

### Provide Preferred Scheme

```kotlin
viewModelScope.launch {
    with(ProcessOut.instance.dispatchers.cardTokenization) {
        preferredSchemeRequest.collect { request ->
            // Inspect issuer information to choose a default preferred scheme.
            val preferredScheme = when (request.issuerInformation.scheme) {
                "visa", "mastercard" -> request.issuerInformation.coScheme
                else -> request.issuerInformation.scheme
            }
            // Send the response with preferred scheme which must be constructed from request.
            // Note that once you've subscribed to 'preferredSchemeRequest' it's required to send response back.
            // Implementation will use primary scheme if 'preferredScheme' is null.
            preferredScheme(request.toResponse(preferredScheme = preferredScheme))
        }
    }
}
```

### Process Tokenized Card

```kotlin
// 1) Subscribe to additionally process tokenized card before completion (e.g. authorize invoice or assign customer token).

lifecycleScope.launch {
    ProcessOut.instance.dispatchers.cardTokenization
        .processTokenizedCardRequest.collect { request ->
            ProcessOut.instance.invoices.authorizeInvoice(
                request = POInvoiceAuthorizationRequest(
                    invoiceId = "iv_",
                    source = request.card.id
                ),
                threeDSService = create3DSService()
            )
        }
}

// 2) Once you've subscribed it's required to call [complete] after processing.

lifecycleScope.launch {
    ProcessOut.instance.invoices
        .authorizeInvoiceResult.collect { result ->
            ProcessOut.instance.dispatchers.cardTokenization.complete(result)
        }
}
```
