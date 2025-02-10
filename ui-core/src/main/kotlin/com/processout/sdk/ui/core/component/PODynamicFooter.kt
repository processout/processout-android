package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POIme.isImeVisibleAsState

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODynamicFooter(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacerBackgroundColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        content()
        val isImeVisible by isImeVisibleAsState()
        val imePaddingValues = WindowInsets.ime.asPaddingValues()
        val navigationBarPaddingValues = WindowInsets.navigationBars.asPaddingValues()
        Spacer(
            Modifier
                .fillMaxWidth()
                .requiredHeight(
                    if (isImeVisible) imePaddingValues.calculateBottomPadding()
                    else navigationBarPaddingValues.calculateBottomPadding()
                )
                .background(spacerBackgroundColor)
        )
    }
}
