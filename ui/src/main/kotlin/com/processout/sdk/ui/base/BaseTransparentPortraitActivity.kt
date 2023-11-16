package com.processout.sdk.ui.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal abstract class BaseTransparentPortraitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is a workaround for crash on Android 8.0 (API level 26).
        // https://issuetracker.google.com/issues/68454482
        // https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            ActivityInfo.SCREEN_ORIENTATION_BEHIND
        else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
