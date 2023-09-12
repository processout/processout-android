# Module ProcessOut Android SDK

## Initialization

```kotlin
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutConfiguration

ProcessOut.configure(
    ProcessOutConfiguration(
        application = this,
        projectId = "your_project_id",
        debug = true // Optionally enable debug mode for logs
    )
)
```

### Access initialized singleton instances

```kotlin
ProcessOut.instance

// Legacy deprecated ProcessOut
ProcessOut.legacyInstance
```
