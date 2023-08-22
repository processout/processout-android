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
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityContract.Companion.EXTRA_RESULT

class POWebViewAuthorizationActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is a workaround for crash on Android 8.0 (API level 26).
        // https://issuetracker.google.com/issues/68454482
        // https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            ActivityInfo.SCREEN_ORIENTATION_BEHIND else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dispatchBackPressed()
        intent.getParcelableExtra<WebViewConfiguration>(EXTRA_CONFIGURATION)
            ?.let { configuration ->
                setContentView(ProcessOutWebView(this, configuration) {
                    finishWithActivityResult(it.toActivityResult())
                })
            }
    }

    private fun dispatchBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finishWithActivityResult(
                ProcessOutActivityResult.Failure(
                    POFailure.Code.Cancelled,
                    "Cancelled by user with back press or gesture."
                ).also { POLogger.debug("%s", it) }
            )
        }
    }

    private fun finishWithActivityResult(result: ProcessOutActivityResult<Uri>) {
        if (!isFinishing) {
            when (result) {
                is ProcessOutActivityResult.Success -> setActivityResult(Activity.RESULT_OK, result)
                is ProcessOutActivityResult.Failure -> setActivityResult(Activity.RESULT_CANCELED, result)
            }
            finish()
        }
    }

    private fun setActivityResult(resultCode: Int, result: ProcessOutActivityResult<Uri>) {
        setResult(resultCode, Intent().putExtra(EXTRA_RESULT, result))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
