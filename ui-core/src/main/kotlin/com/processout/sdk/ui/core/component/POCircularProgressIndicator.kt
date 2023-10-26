package com.processout.sdk.ui.core.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object POCircularProgressIndicator {

    @Composable
    fun Small(color: Color) = CircularProgressIndicator(
        modifier = Modifier.size(18.dp),
        strokeWidth = 1.dp,
        color = color
    )

    @Composable
    fun Medium(color: Color) = CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        strokeWidth = 3.dp,
        color = color
    )
}
