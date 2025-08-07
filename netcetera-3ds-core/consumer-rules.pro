-keep class com.processout.sdk.netcetera.threeds.core.MainSecrets {
    <init>();
}

-keepclassmembers class com.processout.sdk.netcetera.threeds.core.MainSecrets {
    native <methods>;
}

-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder
