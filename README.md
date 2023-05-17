# ProcessOut

## Requirements

*Android 5.0 (API level 21) +*

## Download

Download the latest version from Maven Central.

[ProcessOut Android SDK](https://central.sonatype.com/artifact/com.processout/processout-android)\
`implementation 'com.processout:processout-android:<version>'`

[ProcessOut Android SDK - Checkout 3DS](https://central.sonatype.com/artifact/com.processout/processout-android-checkout-3ds)\
`implementation 'com.processout:processout-android-checkout-3ds:<version>'`

Checkout 3DS SDK requires GitHub Packages authentication:

```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/checkout/checkout-3ds-sdk-android")
        credentials {
            username = "" // GitHub username
            password = "" // GitHub personal access token
        }
    }
}
```

Older versions up to `4.0.0` (including) only [available on the JitPack](https://jitpack.io/#processout/processout-android).

## Documentation

Check the documentation:\
[ProcessOut Android SDK](sdk/dokka/ProcessOut.md)\
[ProcessOut Android SDK - Checkout 3DS](checkout-3ds/dokka/ProcessOutCheckout3DS.md)

## License

ProcessOut is available under the MIT license. See the [LICENSE](LICENSE) file for more info.
