package com.processout.sdk.ui.core.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
        modifier = Modifier.requiredSize(16.dp),
        strokeWidth = 2.dp,
        color = color
    )

    @Composable
    fun Medium(color: Color) = CircularProgressIndicator(
        modifier = Modifier.requiredSize(20.dp),
        strokeWidth = 2.dp,
        color = color
    )

    @Composable
    fun Large(color: Color) = CircularProgressIndicator(
        modifier = Modifier
            .padding(4.dp)
            .requiredSize(28.dp),
        strokeWidth = 3.dp,
        color = color
    )
}
