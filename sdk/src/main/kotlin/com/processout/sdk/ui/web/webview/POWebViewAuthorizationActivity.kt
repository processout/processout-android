package com.processout.sdk.ui.web.webview

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.processout.sdk.R
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.web.ActivityResultDispatcher
import com.processout.sdk.ui.web.POActivityResultApi.Android
import com.processout.sdk.ui.web.POActivityResultApi.Dispatcher
import com.processout.sdk.ui.web.WebAuthorizationActivityResultDispatcher
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivityContract.Companion.EXTRA_FORCE_FINISH
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivityContract.Companion.EXTRA_RESULT

/**
 * Activity that handles 3DS and APM authorization in the WebView.
 * Used internally as a fallback option when Custom Chrome Tabs is not available on the device.
 */
class POWebViewAuthorizationActivity : AppCompatActivity() {

    private val resultDispatcher: ActivityResultDispatcher<Uri> = WebAuthorizationActivityResultDispatcher
    private lateinit var configuration: POWebViewConfiguration

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(EXTRA_FORCE_FINISH, false)) {
            POLogger.info("Activity is started to clear the back stack and finished immediately before it's created.")
            finish()
            return
        }

        intent.getParcelableExtra<POWebViewConfiguration>(EXTRA_CONFIGURATION)
            ?.let { configuration = it }

        if (!::configuration.isInitialized) {
            POLogger.info("Configuration is not provided. Activity is finished immediately before it's created.")
            finish()
            return
        }

        dispatchBackPressed()
        setContentView(ProcessOutWebView(this, configuration) {
            finishWithActivityResult(it.toActivityResult())
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_FORCE_FINISH, false)) {
            finishWithActivityResult(
                ProcessOutActivityResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with cancel action."
                ).also { POLogger.info("%s", it) }
            )
        }
    }

    private fun dispatchBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finishWithActivityResult(
                ProcessOutActivityResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with back press or gesture."
                ).also { POLogger.info("%s", it) }
            )
        }
    }

    private fun finishWithActivityResult(result: ProcessOutActivityResult<Uri>) {
        if (!isFinishing) {
            when (configuration.resultApi) {
                Android -> when (result) {
                    is ProcessOutActivityResult.Success -> setActivityResult(RESULT_OK, result)
                    is ProcessOutActivityResult.Failure -> setActivityResult(RESULT_CANCELED, result)
                }
                Dispatcher -> resultDispatcher.dispatch(result)
            }
            finish()
        }
    }

    private fun setActivityResult(resultCode: Int, result: ProcessOutActivityResult<Uri>) {
        setResult(resultCode, Intent().putExtra(EXTRA_RESULT, result))
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.po_slide_in_left,
                R.anim.po_slide_out_right
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.po_slide_in_left, R.anim.po_slide_out_right)
        }
    }
}
