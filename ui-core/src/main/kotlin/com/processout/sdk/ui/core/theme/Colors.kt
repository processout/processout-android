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
    val checkRadio: CheckRadio,
    val button: Button,
    val surface: Surface,
    val border: Border
) {
    @Immutable
    data class Text(
        val primary: Color,
        val secondary: Color,
        val inverse: Color,
        val muted: Color,
        val placeholder: Color,
        val disabled: Color,
        val onButtonDisabled: Color,
        val success: Color,
        val error: Color,
        val onTipError: Color
    )

    @Immutable
    data class Input(
        val backgroundDefault: Color,
        val backgroundDisabled: Color,
        val borderDefault: Color,
        val borderDefault2: Color,
        val borderDisabled: Color,
        val borderFocused: Color,
        val borderError: Color
    )

    @Immutable
    data class CheckRadio(
        val iconDefault: Color,
        val iconActive: Color,
        val iconError: Color,
        val iconDisabled: Color,
        val borderDefault: Color,
        val borderActive: Color,
        val borderError: Color,
        val borderDisabled: Color,
        val surfaceDefault: Color,
        val surfaceActive: Color,
        val surfaceError: Color,
        val surfaceDisabled: Color
    )

    @Immutable
    data class Button(
        val primaryBackgroundDefault: Color,
        val primaryBackgroundDisabled: Color,
        val primaryBackgroundPressed: Color,
        val secondaryBackgroundDefault: Color,
        val secondaryBackgroundDisabled: Color,
        val secondaryBackgroundPressed: Color,
        val ghostBackgroundDisabled: Color,
        val ghostBackgroundPressed: Color
    )

    @Immutable
    data class Surface(
        val default: Color,
        val neutral: Color,
        val darkout: Color,
        val darkoutRipple: Color,
        val success: Color,
        val error: Color,
        val toastError: Color
    )

    @Immutable
    data class Border(
        val subtle: Color
    )
}

@ProcessOutInternalApi
val POLightColorPalette = POColors(
    text = Text(
        primary = Color(0xFF000000),
        secondary = Color(0xFF585A5F),
        inverse = Color(0xFFFFFFFF),
        muted = Color(0xFF5B6576),
        placeholder = Color(0xFF707378),
        disabled = Color(0xFFADB5BD),
        onButtonDisabled = Color(0xFFC0C3C8),
        success = Color(0xFF00291D),
        error = Color(0xFFBE011B),
        onTipError = Color(0xFF630407)
    ),
    input = Input(
        backgroundDefault = Color(0xFFFFFFFF),
        backgroundDisabled = Color(0x0F121314),
        borderDefault = Color(0xFF7C8593),
        borderDefault2 = Color(0x1F121314),
        borderDisabled = Color(0xFFADB5BD),
        borderFocused = Color(0xFF4791FF),
        borderError = Color(0xFFBE011B)
    ),
    checkRadio = CheckRadio(
        iconDefault = Color(0xFFFFFFFF),
        iconActive = Color(0xFFFFFFFF),
        iconError = Color(0xFFF03030),
        iconDisabled = Color(0xFFC0C3C8),
        borderDefault = Color(0xFFC0C3C8),
        borderActive = Color(0xFF000000),
        borderError = Color(0xFFF03030),
        borderDisabled = Color(0xFFE2E2E2),
        surfaceDefault = Color(0xFFFFFFFF),
        surfaceActive = Color(0xFF000000),
        surfaceError = Color(0xFFFDE3DE),
        surfaceDisabled = Color(0xFFF1F1F2)
    ),
    button = Button(
        primaryBackgroundDefault = Color(0xFF000000),
        primaryBackgroundDisabled = Color(0x0A121314),
        primaryBackgroundPressed = Color(0xFF26292F),
        secondaryBackgroundDefault = Color(0x0F121314),
        secondaryBackgroundDisabled = Color(0x0A121314),
        secondaryBackgroundPressed = Color(0x29212222),
        ghostBackgroundDisabled = Color(0x0A121314),
        ghostBackgroundPressed = Color(0x1F121314)
    ),
    surface = Surface(
        default = Color(0xFFFFFFFF),
        neutral = Color(0xFFFAFAFA),
        darkout = Color(0x0F121314),
        darkoutRipple = Color(0x0F888989),
        success = Color(0xFFBEFAE9),
        error = Color(0xFFFFC2C8),
        toastError = Color(0xFFFDE3DE)
    ),
    border = Border(
        subtle = Color(0xFFCCD1D6)
    )
)

@ProcessOutInternalApi
val PODarkColorPalette = POColors(
    text = Text(
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFC0C3C8),
        inverse = Color(0xFF000000),
        muted = Color(0xFFADB5BD),
        placeholder = Color(0xFFA7A9AF),
        disabled = Color(0xFF5B6576),
        onButtonDisabled = Color(0xFF707378),
        success = Color(0xFFE5FFF8),
        error = Color(0xFFFF7D6C),
        onTipError = Color(0xFFF5D9D9)
    ),
    input = Input(
        backgroundDefault = Color(0xFF26292F),
        backgroundDisabled = Color(0x14F6F8FB),
        borderDefault = Color(0xFFCCD1D6),
        borderDefault2 = Color(0x29F6F8FB),
        borderDisabled = Color(0xFF7C8593),
        borderFocused = Color(0xFFFFE500),
        borderError = Color(0xFFFF7D6C)
    ),
    checkRadio = CheckRadio(
        iconDefault = Color(0x33121314),
        iconActive = Color(0xFF000000),
        iconError = Color(0xFFFF7D6C),
        iconDisabled = Color(0xFF707378),
        borderDefault = Color(0x3DF6F8FB),
        borderActive = Color(0xFFFFFFFF),
        borderError = Color(0xFFFF7D6C),
        borderDisabled = Color(0xFF3D4149),
        surfaceDefault = Color(0x33121314),
        surfaceActive = Color(0xFFFFFFFF),
        surfaceError = Color(0xFF3D0D04),
        surfaceDisabled = Color(0xFF2E3137)
    ),
    button = Button(
        primaryBackgroundDefault = Color(0xFFFFFFFF),
        primaryBackgroundDisabled = Color(0xFF2E3137),
        primaryBackgroundPressed = Color(0xFF585A5F),
        secondaryBackgroundDefault = Color(0x14F6F8FB),
        secondaryBackgroundDisabled = Color(0xFF2E3137),
        secondaryBackgroundPressed = Color(0x0FF6F8FB),
        ghostBackgroundDisabled = Color(0xFF2E3137),
        ghostBackgroundPressed = Color(0x1FF6F8FB)
    ),
    surface = Surface(
        default = Color(0xFF26292F),
        neutral = Color(0xFF2A2D34),
        darkout = Color(0x0FF6F8FB),
        darkoutRipple = Color(0x0F7B7C7D),
        success = Color(0xFF1DA37D),
        error = Color(0xFFD11D2F),
        toastError = Color(0xFF511511)
    ),
    border = Border(
        subtle = Color(0xFF7C8593)
    )
)

internal val LocalPOColors = staticCompositionLocalOf { POLightColorPalette }
