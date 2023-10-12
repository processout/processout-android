package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POColors(
    val text: Text,
    val action: Action,
    val surface: Surface,
    val border: Border
) {
    @Immutable
    data class Text(
        val primary: Color,
        val secondary: Color,
        val tertiary: Color,
        val muted: Color,
        val disabled: Color,
        val onColor: Color,
        val success: Color,
        val warning: Color,
        val error: Color
    )

    @Immutable
    data class Action(
        val primaryDefault: Color,
        val primaryPressed: Color,
        val primaryDisabled: Color,
        val secondaryDefault: Color,
        val secondaryPressed: Color,
        val borderSelected: Color,
        val borderDisabled: Color
    )

    @Immutable
    data class Surface(
        val background: Color,
        val level1: Color,
        val neutral: Color,
        val success: Color,
        val warning: Color,
        val error: Color
    )

    @Immutable
    data class Border(
        val default: Color,
        val divider: Color,
        val subtle: Color
    )
}

internal val LightColorPalette = POColors(
    text = POColors.Text(
        primary = Color(0xFF121212),
        secondary = Color(0xFF313135),
        tertiary = Color(0xFF4E4E55),
        muted = Color(0xFF67676F),
        disabled = Color(0xFF8D8D95),
        onColor = Color(0xFFFCFCFC),
        success = Color(0xFF014B21),
        warning = Color(0xFF742702),
        error = Color(0xFF7B0F17)
    ),
    action = POColors.Action(
        primaryDefault = Color(0xFF2B2A93),
        primaryPressed = Color(0xFF1E1E76),
        primaryDisabled = Color(0xFFE5E5E7),
        secondaryDefault = Color(0xFFFFFFFF),
        secondaryPressed = Color(0xFFE5E5E7),
        borderSelected = Color(0xFF4E4E55),
        borderDisabled = Color(0xFFC4C4C8)
    ),
    surface = POColors.Surface(
        background = Color(0xFFFFFFFF),
        level1 = Color(0xFFFCFCFD),
        neutral = Color(0xFFF7F7F8),
        success = Color(0xFFD8F8E5),
        warning = Color(0xFFFDEBE3),
        error = Color(0xFFFAD1D4)
    ),
    border = POColors.Border(
        default = Color(0xFF8D8D95),
        divider = Color(0xFFF2F2F3),
        subtle = Color(0xFFE5E5E7)
    )
)

internal val DarkColorPalette = POColors(
    text = POColors.Text(
        primary = Color(0xFFF8F8FB),
        secondary = Color(0xFFD4D4DD),
        tertiary = Color(0xFFBABAC5),
        muted = Color(0xFF747581),
        disabled = Color(0xFF4D4F5B),
        onColor = Color(0xFFF8F8FB),
        success = Color(0xFFD8F8E5),
        warning = Color(0xFFFDEBE3),
        error = Color(0xFFFBE9EB)
    ),
    action = POColors.Action(
        primaryDefault = Color(0xFF6A64D8),
        primaryPressed = Color(0xFF4D49C5),
        primaryDisabled = Color(0xFF2B2D3A),
        secondaryDefault = Color(0xFF121421),
        secondaryPressed = Color(0xFF181A2A),
        borderSelected = Color(0xFFBABAC5),
        borderDisabled = Color(0xFF363945)
    ),
    surface = POColors.Surface(
        background = Color(0xFF121421),
        level1 = Color(0xFF111322),
        neutral = Color(0xFF181A2A),
        success = Color(0xFF014B21),
        warning = Color(0xFFA23807),
        error = Color(0xFFC0212B)
    ),
    border = POColors.Border(
        default = Color(0xFF93949F),
        divider = Color(0xFF212431),
        subtle = Color(0xFF2B2D3A)
    )
)

internal val LocalPOColors = staticCompositionLocalOf { LightColorPalette }
