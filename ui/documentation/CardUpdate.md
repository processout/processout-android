# Module ProcessOut Android SDK - UI

## Card Update

### Launch Card Update Bottom Sheet

```kotlin
// 1) It is required to initialize launcher in onCreate() method of Activity or Fragment.

private lateinit var launcher: POCardUpdateLauncher

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = POCardUpdateLauncher.create(from = this) { result ->
        result.onSuccess { card ->
            TODO()
        }.onFailure { TODO() }
    }
}

// 2) Launch the activity.

launcher.launch(
    POCardUpdateConfiguration(
        cardId = "card_"
    )
)
```

### Configuration

```kotlin
POCardUpdateConfiguration(
    cardId = "card_",
    options = POCardUpdateConfiguration.Options(
        title = "Payment Details",
        cardInformation = POCardUpdateConfiguration.CardInformation(
            maskedNumber = "4010 **** **** **21",
            iin = "401000",
            scheme = "visa",
            preferredScheme = "carte bancaire"
        ),
        primaryActionText = "Submit",
        secondaryActionText = "Cancel",
        cancellation = POCancellationConfiguration(
            secondaryAction = true,
            backPressed = false,
            dragDown = true,
            touchOutside = false
        )
    ),
    style = POCardUpdateConfiguration.Style(
        // Customize the look and feel.
    )
)
```

### Error Handling

```kotlin
viewModelScope.launch {
    with(ProcessOut.instance.dispatchers.cardUpdate) {
        shouldContinueRequest.collect { request ->
            // Inspect the failure to decide whether the flow should continue or complete.
            val shouldContinue = when (val code = request.failure.code) {
                is Generic -> when (code.genericCode) {
                    requestInvalidCard,
                    cardInvalid,
                    cardBadTrackData,
                    cardMissingCvc,
                    cardInvalidCvc,
                    cardFailedCvc,
                    cardFailedCvcAndAvs -> true
                    else -> false
                }
                else -> false
            }

            // Notify by sending the response which must be constructed from request.
            // Note that once you've subscribed to 'shouldContinueRequest'
            // it's required to send response back otherwise the card update flow will not proceed.
            shouldContinue(request.toResponse(shouldContinue = shouldContinue))
        }
    }
}

```

### Lifecycle Events

```kotlin
viewModelScope.launch {
    ProcessOut.instance.dispatchers.cardUpdate
        .events.collect { event ->
            when (event) {
                POCardUpdateEvent.DidStart -> TODO()
                POCardUpdateEvent.ParametersChanged -> TODO()
                POCardUpdateEvent.WillUpdateCard -> TODO()
                POCardUpdateEvent.DidComplete -> TODO()
            }
        }
}
```
