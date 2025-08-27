package com.processout.sdk.ui.savedpaymentmethods

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.POBaseTransparentPortraitActivity
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_FORCE_FINISH

internal class SavedPaymentMethodsActivity : POBaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(EXTRA_FORCE_FINISH, false)) {
            finish()
            return
        }
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(SavedPaymentMethodsBottomSheet.tag)
            if (bottomSheet == null) {
                SavedPaymentMethodsBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, SavedPaymentMethodsBottomSheet.tag)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_FORCE_FINISH, false)) {
            forceFinish()
        }
    }

    private fun forceFinish() {
        val bottomSheet = supportFragmentManager.findFragmentByTag(SavedPaymentMethodsBottomSheet.tag)
        if (bottomSheet == null) {
            finish()
            return
        }
        (bottomSheet as SavedPaymentMethodsBottomSheet).dismiss(
            ProcessOutResult.Failure(
                code = Cancelled,
                message = "Cancelled: finished programmatically."
            )
        )
    }
}
