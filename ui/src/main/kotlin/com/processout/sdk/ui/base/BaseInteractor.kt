package com.processout.sdk.ui.base

import com.processout.sdk.core.coroutine.POCloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal abstract class BaseInteractor(
    protected val interactorScope: POCloseableCoroutineScope =
        POCloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
)
