package com.processout.example.service.threeds

import android.app.Activity
import android.net.Uri
import com.checkout.threeds.domain.model.UICustomization
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.ThreeDS2ServiceConfiguration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.checkout.threeds.POCheckout3DSServiceDelegate
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher
import java.util.Locale

class Checkout3DSServiceDelegate(
    private val activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher,
    private val returnUrl: String
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

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        customTabLauncher.launch(redirect, returnUrl, callback)
    }
}
