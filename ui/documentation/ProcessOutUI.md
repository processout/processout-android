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
        title = "Payment details",
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
