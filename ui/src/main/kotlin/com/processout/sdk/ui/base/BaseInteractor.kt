package com.processout.sdk.ui.base

import com.processout.sdk.core.coroutines.POCloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal abstract class BaseInteractor(
    val interactorScope: POCloseableCoroutineScope =
        POCloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
)
