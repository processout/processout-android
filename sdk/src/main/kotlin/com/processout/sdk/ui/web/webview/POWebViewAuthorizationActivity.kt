package com.processout.sdk.ui.web.webview

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.processout.sdk.ui.web.POActivityResultApi.Android
import com.processout.sdk.ui.web.POActivityResultApi.Dispatcher
import com.processout.sdk.ui.web.ActivityResultDispatcher
import com.processout.sdk.ui.web.WebAuthorizationActivityResultDispatcher
import com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION
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
        // This is a workaround for crash on Android 8.0 (API level 26).
        // https://issuetracker.google.com/issues/68454482
        // https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            ActivityInfo.SCREEN_ORIENTATION_BEHIND else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        intent.getParcelableExtra<POWebViewConfiguration>(EXTRA_CONFIGURATION)
            ?.let { configuration = it }
        dispatchBackPressed()
        setContentView(ProcessOutWebView(this, configuration) {
            finishWithActivityResult(it.toActivityResult())
        })
    }

    private fun dispatchBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finishWithActivityResult(
                ProcessOutActivityResult.Failure(
                    POFailure.Code.Cancelled,
                    "Cancelled by user with back press or gesture."
                ).also { POLogger.info("%s", it) }
            )
        }
    }

    private fun finishWithActivityResult(result: ProcessOutActivityResult<Uri>) {
        if (!isFinishing) {
            when (configuration.resultApi) {
                Android -> when (result) {
                    is ProcessOutActivityResult.Success -> setActivityResult(Activity.RESULT_OK, result)
                    is ProcessOutActivityResult.Failure -> setActivityResult(Activity.RESULT_CANCELED, result)
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
                Activity.OVERRIDE_TRANSITION_CLOSE,
                R.anim.po_slide_in_left,
                R.anim.po_slide_out_right
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.po_slide_in_left, R.anim.po_slide_out_right)
        }
    }
}
