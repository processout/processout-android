package com.processout.sdk.ui.shared.component

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.processout.sdk.ui.shared.component.ScreenMode.Fullscreen
import com.processout.sdk.ui.shared.component.ScreenMode.Window
import com.processout.sdk.ui.shared.extension.screenSize

internal sealed interface ScreenMode {
    data class Window(val height: Int, val availableHeight: Int) : ScreenMode
    data class Fullscreen(val screenHeight: Int) : ScreenMode
}

@Composable
internal fun screenModeAsState(viewHeight: Int): State<ScreenMode> {
    val isImeVisible by isImeVisibleAsState()
    val totalViewHeight = if (isImeVisible) viewHeight + imeHeight() else viewHeight
    val screenHeight = LocalContext.current.screenSize().height + navigationBarHeight()
    val screenMode = remember(totalViewHeight, screenHeight) {
        mutableStateOf<ScreenMode>(Window(height = totalViewHeight, availableHeight = screenHeight))
    }
    if (screenHeight < totalViewHeight) {
        screenMode.value = Fullscreen(screenHeight)
    }
    return screenMode
}

@Composable
internal fun navigationBarHeight(): Int =
    ViewCompat.getRootWindowInsets(LocalView.current.rootView)
        ?.getInsets(WindowInsetsCompat.Type.navigationBars())
        ?.bottom ?: 0
