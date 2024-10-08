package com.processout.sdk.ui.shared.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun DynamicFooter(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
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
            Modifier.requiredHeight(
                if (isImeVisible) imePaddingValues.calculateBottomPadding()
                else navigationBarPaddingValues.calculateBottomPadding()
            )
        )
    }
}
