package com.processout.sdk.ui.shared.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

internal fun <T, R> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> R
): StateFlow<R> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)

internal fun <T> Flow<T>.distinctUntilChangedByMultiple(
    vararg selectors: (T) -> Any?
): Flow<T> = this
    .map { value -> selectors.map { it(value) } to value }
    .distinctUntilChanged { old, new -> old.first == new.first }
    .map { it.second }

@Composable
@SuppressLint("ComposableNaming")
internal fun <T> Flow<T>.collectImmediately(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    LaunchedEffect(lifecycleOwner, minActiveState) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            withContext(Dispatchers.Main.immediate) {
                collect(collector)
            }
        }
    }
}
