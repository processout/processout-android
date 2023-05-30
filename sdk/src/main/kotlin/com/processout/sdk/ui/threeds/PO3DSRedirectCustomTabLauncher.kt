package com.processout.sdk.ui.threeds

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult

class PO3DSRedirectCustomTabLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<PO3DSRedirect>

    private var callback: ((ProcessOutResult<String>) -> Unit)? = null

    companion object {
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTab3DSAuthorizationActivityContract(),
                activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTab3DSAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
        }
    }

    fun launch(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        this.callback = callback
        launcher.launch(redirect)
    }

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<ThreeDSToken>> {
        when (it) {
            is ProcessOutActivityResult.Success ->
                callback?.invoke(ProcessOutResult.Success(it.value.token))
            is ProcessOutActivityResult.Failure ->
                callback?.invoke(ProcessOutResult.Failure(it.code, it.message))
        }
    }
}
