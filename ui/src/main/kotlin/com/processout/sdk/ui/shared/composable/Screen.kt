package com.processout.sdk.ui.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.shared.composable.ScreenMode.Fullscreen
import com.processout.sdk.ui.shared.composable.ScreenMode.Window

internal sealed interface ScreenMode {
    data class Window(val height: Int) : ScreenMode
    data class Fullscreen(val screenHeight: Int) : ScreenMode
}

@Composable
internal fun screenModeAsState(height: Int): State<ScreenMode> {
    val screenMode = remember { mutableStateOf<ScreenMode>(Window(height)) }
    val imeVisible = imeVisibleAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp.dpToPx()
    val availableHeight = if (imeVisible) screenHeight - imeHeight() else screenHeight
    if (availableHeight < height) {
        screenMode.value = Fullscreen(screenHeight)
    } else {
        screenMode.value = Window(height)
    }
    return screenMode
}
