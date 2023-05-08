# Module ProcessOut Android SDK

## Initialization
```
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutConfiguration

ProcessOut.configure(
    ProcessOutConfiguration(
        application = this,
        projectId = "your_project_id"
    )
)
```

### Access initialized singleton instances
```
ProcessOut.instance

// Legacy deprecated ProcessOut
ProcessOut.legacyInstance
```
