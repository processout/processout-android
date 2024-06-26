package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POColors2(
    val text: Text,
    val input: Input,
    val button: Button,
    val surface: Surface,
    val border: Border
) {
    @Immutable
    data class Text(
        val primary: Color,
        val inverse: Color,
        val muted: Color,
        val disabled: Color,
        val success: Color,
        val error: Color
    )

    @Immutable
    data class Input(
        val backgroundDefault: Color,
        val backgroundDisabled: Color,
        val borderDefault: Color,
        val borderDisabled: Color,
        val borderFocused: Color,
        val borderError: Color
    )

    @Immutable
    data class Button(
        val primaryBackgroundDefault: Color,
        val primaryBackgroundPressed: Color,
        val primaryBackgroundDisabled: Color,
        val secondaryBackgroundDefault: Color,
        val secondaryBackgroundPressed: Color,
        val secondaryBackgroundDisabled: Color,
        val secondaryBorderDefault: Color,
        val secondaryBorderPressed: Color,
        val secondaryBorderDisabled: Color
    )

    @Immutable
    data class Surface(
        val default: Color,
        val neutral: Color,
        val success: Color,
        val error: Color
    )

    @Immutable
    data class Border(
        val default: Color,
        val disabled: Color,
        val subtle: Color
    )
}
