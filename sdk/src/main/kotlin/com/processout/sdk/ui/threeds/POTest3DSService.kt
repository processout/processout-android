package com.processout.sdk.ui.threeds

import android.app.Activity
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.view.dialog.POAlertDialog

/**
 * Service that emulates the normal 3DS authentication flow
 * but does not actually make any calls to a real Access Control Server (ACS).
 * Should be used only for testing purposes in sandbox environment.
 */
class POTest3DSService(
    private val activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher? = null,
    private val returnUrl: String = String()
) : PO3DSService {

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
        POAlertDialog(
            context = activity,
            title = POTest3DSService::class.java.simpleName,
            message = "Authorize native 3DS2 challenge?",
            confirmActionText = "Yes",
            dismissActionText = "No"
        ).onConfirmButtonClick { dialog ->
            dialog.dismiss()
            callback(ProcessOutResult.Success(true))
        }.onDismissButtonClick { dialog ->
            dialog.dismiss()
            callback(ProcessOutResult.Success(false))
        }.show()
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
