package com.processout.sdk.ui.threeds

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult

/**
 * Service that emulates the normal 3DS authentication flow
 * but does not actually make any calls to a real Access Control Server (ACS).
 * Should be used only for testing purposes in sandbox environment.
 */
class POTest3DSService(
    activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher? = null,
    private val returnUrl: String = String()
) : PO3DSService {

    private val dialogBuilder = MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_ProcessOut_MaterialAlertDialog)

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
            setCancelable(false)
        }.also { it.show() }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        customTabLauncher?.launch(redirect, returnUrl, callback)
            ?: callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Cancelled,
                    "PO3DSRedirectCustomTabLauncher is not provided."
                )
            )
    }
}
