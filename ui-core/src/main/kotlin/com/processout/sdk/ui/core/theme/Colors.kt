package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.POColors2.*

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POColors(
    val text: Text,
    val button: Button,
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
        val inverse: Color,
        val success: Color,
        val error: Color
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

internal val LightColorPalette = POColors2(
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
        primaryBackgroundPressed = Color(0xFF242C38),
        primaryBackgroundDisabled = Color(0xFFEDEEEF),
        secondaryBackgroundDefault = Color(0xFFFFFFFF),
        secondaryBackgroundPressed = Color(0xFFCCD1D6),
        secondaryBackgroundDisabled = Color(0xFFFFFFFF),
        secondaryBorderDefault = Color(0xFF121821),
        secondaryBorderPressed = Color(0xFF242C38),
        secondaryBorderDisabled = Color(0xFFEDEEEF)
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

internal val DarkColorPalette = POColors2(
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
        primaryBackgroundPressed = Color(0xFFCCD1D6),
        primaryBackgroundDisabled = Color(0xFF242C38),
        secondaryBackgroundDefault = Color(0xFF121821),
        secondaryBackgroundPressed = Color(0xFF7C8593),
        secondaryBackgroundDisabled = Color(0xFF121821),
        secondaryBorderDefault = Color(0xFFFFFFFF),
        secondaryBorderPressed = Color(0xFFCCD1D6),
        secondaryBorderDisabled = Color(0xFF242C38)
    ),
    surface = Surface(
        default = Color(0xFF121821),
        neutral = Color(0xFF242C38),
        success = Color(0xFFBEFAE9),
        error = Color(0xFFFFC2C8)
    ),
    border = Border(
        default = Color(0xFF93949F),
        disabled = Color(0xFF363945),
        subtle = Color(0xFF5B6576)
    )
)

internal val LocalPOColors = staticCompositionLocalOf { LightColorPalette }
