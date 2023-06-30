package com.processout.sdk.ui.threeds

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.ProcessOutResult

class POTest3DSService(
    activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher? = null
) : PO3DSService {

    private val dialogBuilder = AlertDialog.Builder(activity)

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        val request = PO3DS2AuthenticationRequest(
            deviceData = String(),
            sdkAppId = String(),
            sdkEphemeralPublicKey = String(),
            sdkReferenceNumber = String(),
            sdkTransactionId = String()
        )
        callback(ProcessOutResult.Success(request))
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (ProcessOutResult<Boolean>) -> Unit) {
        with(dialogBuilder) {
            setTitle(ProcessOut.NAME)
            setMessage("Authorize mobile 3DS2 challenge?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                callback(ProcessOutResult.Success(true))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                callback(ProcessOutResult.Success(false))
            }
        }.also { it.show() }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        customTabLauncher?.launch(redirect, returnUrl = String()) { result ->
            callback(result)
        }
    }
}
