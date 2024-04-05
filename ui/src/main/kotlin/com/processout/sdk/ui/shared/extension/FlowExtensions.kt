package com.processout.sdk.ui.shared.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal fun <T, R> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> R
): StateFlow<R> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)
