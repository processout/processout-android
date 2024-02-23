package com.processout.sdk.ui.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun Modifier.conditional(
    condition: Boolean,
    modifier: @Composable Modifier.() -> Modifier
) = if (condition) then(modifier(Modifier)) else this

@Composable
internal fun Modifier.conditional(
    condition: Boolean,
    whenTrue: @Composable Modifier.() -> Modifier,
    whenFalse: @Composable Modifier.() -> Modifier
) = when (condition) {
    true -> then(whenTrue(Modifier))
    false -> then(whenFalse(Modifier))
}
