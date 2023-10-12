package com.processout.sdk.ui.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Composable
fun ProcessOutTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette
    val typography = ProcessOutTheme.typography
    CompositionLocalProvider(
        LocalPOColors provides colors,
        LocalPOTypography provides typography,
        LocalPOShapes provides ProcessOutTheme.shapes,
        LocalPODimensions provides ProcessOutTheme.dimensions
    ) {
        ProvideTextStyle(
            value = typography.fixed.body
                .copy(color = colors.textPrimary),
            content = content
        )
    }
}

/** @suppress */
@ProcessOutInternalApi
object ProcessOutTheme {
    val colors: POColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPOColors.current

    val typography: POTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPOTypography.current

    val shapes: POShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalPOShapes.current

    val dimensions: PODimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalPODimensions.current
}
