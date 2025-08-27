package com.processout.example.service.threeds

import androidx.activity.ComponentActivity
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.netcetera.threeds.PONetcetera3DS2ServiceDelegate
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher

class Netcetera3DS2ServiceDelegate(
    private val provideActivity: () -> ComponentActivity?,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher,
    private val returnUrl: String
) : PONetcetera3DS2ServiceDelegate {

    override fun activity(): ComponentActivity? = provideActivity()

    override fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        customTabLauncher.launch(redirect, returnUrl, callback)
    }
}
