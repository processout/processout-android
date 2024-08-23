package com.processout.sdk.api.preferences

import android.content.Context
import androidx.core.content.edit
import com.processout.sdk.BuildConfig
import com.processout.sdk.di.ContextGraph
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

internal class Preferences(
    contextGraph: ContextGraph,
    scope: CoroutineScope = contextGraph.mainScope,
    workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private companion object {
        const val FILENAME = "${BuildConfig.LIBRARY_PACKAGE_NAME}.preferences"
        const val KEY_INSTALLATION_ID = "InstallationId"
    }

    private val sharedPreferences = contextGraph.configuration.application
        .getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    var installationId = String()
        private set

    init {
        scope.launch(workDispatcher) {
            installationId = sharedPreferences.getString(KEY_INSTALLATION_ID, String()) ?: String()
            if (installationId.isEmpty()) {
                installationId = UUID.randomUUID().toString()
                sharedPreferences.edit(commit = true) {
                    putString(KEY_INSTALLATION_ID, installationId)
                }
            }
        }
    }
}
