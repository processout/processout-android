package com.processout.sdk.ui.web.customtab

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.sdk.api.service.POBrowserCapabilitiesService.Companion.CHROME_PACKAGE
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract.Companion.EXTRA_TIMEOUT_FINISH
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationUiState.*
import kotlinx.coroutines.launch

class POCustomTabAuthorizationActivity : AppCompatActivity() {

    private lateinit var configuration: CustomTabConfiguration

    private val viewModel: CustomTabAuthorizationViewModel by viewModels {
        CustomTabAuthorizationViewModel.Factory(configuration)
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is a workaround for crash on Android 8.0 (API level 26).
        // https://issuetracker.google.com/issues/68454482
        // https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            ActivityInfo.SCREEN_ORIENTATION_BEHIND else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        intent.getParcelableExtra<CustomTabConfiguration>(EXTRA_CONFIGURATION)
            ?.let { configuration = it }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { handleUiState(it) }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(intent)
    }

    private fun handleUiState(uiState: CustomTabAuthorizationUiState) {
        when (uiState) {
            is Launching -> launchCustomTab(uiState.uri)
            is Success -> finishWithActivityResult(
                ProcessOutActivityResult.Success(uiState.returnUri)
            )
            Cancelled -> finishWithActivityResult(
                ProcessOutActivityResult.Failure(
                    POFailure.Code.Cancelled,
                    "Cancelled by user with back press or gesture."
                )
            )
            is Timeout -> handleTimeout(uiState.clearBackStack)
            else -> {}
        }
    }

    private fun launchCustomTab(uri: Uri) {
        CustomTabsIntent.Builder()
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()
            .also {
                it.intent.setPackage(CHROME_PACKAGE)
                it.launchUrl(this, uri)
            }
        viewModel.onLaunched()
    }

    private fun handleTimeout(clearBackStack: Boolean) {
        if (clearBackStack) {
            intent.let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                it.putExtra(EXTRA_TIMEOUT_FINISH, true)
                startActivity(it)
            }
        } else {
            finishWithActivityResult(
                ProcessOutActivityResult.Failure(POFailure.Code.Timeout())
            )
        }
    }

    private fun finishWithActivityResult(result: ProcessOutActivityResult<Uri>) {
        when (result) {
            is ProcessOutActivityResult.Success -> setActivityResult(Activity.RESULT_OK, result)
            is ProcessOutActivityResult.Failure -> setActivityResult(Activity.RESULT_CANCELED, result)
        }
        finish()
    }

    private fun setActivityResult(resultCode: Int, result: ProcessOutActivityResult<Uri>) {
        setResult(resultCode, Intent().putExtra(EXTRA_RESULT, result))
    }
}
