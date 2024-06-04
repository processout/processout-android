package com.processout.sdk.ui.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.processout.sdk.ui.shared.component.ScreenMode.Fullscreen
import com.processout.sdk.ui.shared.component.ScreenMode.Window
import com.processout.sdk.ui.shared.extension.dpToPx

internal sealed interface ScreenMode {
    data class Window(val height: Int, val availableHeight: Int) : ScreenMode
    data class Fullscreen(val screenHeight: Int) : ScreenMode
}

@Composable
internal fun screenModeAsState(viewHeight: Int): State<ScreenMode> {
    val isImeVisible = isImeVisibleAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp.dpToPx()
    val availableHeight = if (isImeVisible) screenHeight - imeHeight() + navigationBarHeight() else screenHeight
    val screenMode = remember {
        mutableStateOf<ScreenMode>(Window(height = viewHeight, availableHeight = availableHeight))
    }
    if (availableHeight < viewHeight) {
        screenMode.value = Fullscreen(screenHeight)
    } else {
        screenMode.value = Window(height = viewHeight, availableHeight = availableHeight)
    }
    return screenMode
}

@Composable
internal fun navigationBarHeight(): Int =
    ViewCompat.getRootWindowInsets(LocalView.current.rootView)
        ?.getInsets(WindowInsetsCompat.Type.navigationBars())
        ?.bottom ?: 0
