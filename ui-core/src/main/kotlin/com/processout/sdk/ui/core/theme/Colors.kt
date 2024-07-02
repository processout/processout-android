package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.POColors.*

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POColors(
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
        val primaryBackgroundDisabled: Color,
        val primaryBackgroundPressed: Color,
        val secondaryBackgroundDefault: Color,
        val secondaryBackgroundDisabled: Color,
        val secondaryBackgroundPressed: Color,
        val secondaryBorderDefault: Color,
        val secondaryBorderDisabled: Color,
        val secondaryBorderPressed: Color,
        val tertiaryBackgroundPressed: Color
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

internal val LightColorPalette = POColors(
    text = Text(
        primary = Color(0xFF121821),
        inverse = Color(0xFFFAFAFA),
        muted = Color(0xFF5B6576),
        disabled = Color(0xFFADB5BD),
        success = Color(0xFF00291D),
        error = Color(0xFFD11D2F)
    ),
    input = Input(
        backgroundDefault = Color(0xFFFFFFFF),
        backgroundDisabled = Color(0xFFEDEEEF),
        borderDefault = Color(0xFF7C8593),
        borderDisabled = Color(0xFFADB5BD),
        borderFocused = Color(0xFF4791FF),
        borderError = Color(0xFFD11D2F)
    ),
    button = Button(
        primaryBackgroundDefault = Color(0xFF121821),
        primaryBackgroundDisabled = Color(0xFFEDEEEF),
        primaryBackgroundPressed = Color(0xFF242C38),
        secondaryBackgroundDefault = Color(0xFFFFFFFF),
        secondaryBackgroundDisabled = Color(0xFFFFFFFF),
        secondaryBackgroundPressed = Color(0xFFCCD1D6),
        secondaryBorderDefault = Color(0xFF121821),
        secondaryBorderDisabled = Color(0xFFEDEEEF),
        secondaryBorderPressed = Color(0xFF242C38),
        tertiaryBackgroundPressed = Color(0xFFCCD1D6)
    ),
    surface = Surface(
        default = Color(0xFFFFFFFF),
        neutral = Color(0xFFFAFAFA),
        success = Color(0xFFBEFAE9),
        error = Color(0xFFFFC2C8)
    ),
    border = Border(
        default = Color(0xFF8D8D95),
        disabled = Color(0xFFC4C4C8),
        subtle = Color(0xFFDDE0E3)
    )
)

internal val DarkColorPalette = POColors(
    text = Text(
        primary = Color(0xFFFAFAFA),
        inverse = Color(0xFF121821),
        muted = Color(0xFFADB5BD),
        disabled = Color(0xFF5B6576),
        success = Color(0xFFE5FFF8),
        error = Color(0xFFFF5263)
    ),
    input = Input(
        backgroundDefault = Color(0xFF121821),
        backgroundDisabled = Color(0xFF242C38),
        borderDefault = Color(0xFFCCD1D6),
        borderDisabled = Color(0xFF7C8593),
        borderFocused = Color(0xFFFFE500),
        borderError = Color(0xFFFF5263)
    ),
    button = Button(
        primaryBackgroundDefault = Color(0xFFFFFFFF),
        primaryBackgroundDisabled = Color(0xFF242C38),
        primaryBackgroundPressed = Color(0xFFCCD1D6),
        secondaryBackgroundDefault = Color(0xFF121821),
        secondaryBackgroundDisabled = Color(0xFF121821),
        secondaryBackgroundPressed = Color(0xFF7C8593),
        secondaryBorderDefault = Color(0xFFFFFFFF),
        secondaryBorderDisabled = Color(0xFF242C38),
        secondaryBorderPressed = Color(0xFFCCD1D6),
        tertiaryBackgroundPressed = Color(0xFF7C8593)
    ),
    surface = Surface(
        default = Color(0xFF121821),
        neutral = Color(0xFF242C38),
        success = Color(0xFF1DA37D),
        error = Color(0xFFD11D2F)
    ),
    border = Border(
        default = Color(0xFF93949F),
        disabled = Color(0xFF363945),
        subtle = Color(0xFF5B6576)
    )
)

internal val LocalPOColors = staticCompositionLocalOf { LightColorPalette }
