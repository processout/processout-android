@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object ProcessOutTheme {

    @Composable
    operator fun invoke(
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette
        CompositionLocalProvider(
            LocalPOColors provides colors,
            LocalPOTypography provides typography,
            LocalPOShapes provides shapes,
            LocalPODimensions provides dimensions
        ) {
            ProvideTextStyle(
                value = typography.fixed.body,
                content = content
            )
        }
    }

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
