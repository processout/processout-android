package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POColors(
    val surfaceBackground: Color,
    val surfaceLevel1: Color,
    val surfaceNeutral: Color,
    val surfaceSuccess: Color,
    val surfaceWarning: Color,
    val surfaceError: Color,
    val borderDefault: Color,
    val borderDivider: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val textDisabled: Color,
    val textOnColor: Color,
    val textSuccess: Color,
    val textWarning: Color,
    val textError: Color,
    val actionPrimaryDefault: Color,
    val actionPrimaryPressed: Color,
    val actionPrimaryDisabled: Color,
    val actionSecondaryDefault: Color,
    val actionSecondaryPressed: Color,
    val actionBorderSelected: Color,
    val actionBorderDisabled: Color
)

internal val LightColorPalette = POColors(
    surfaceBackground = Color(0xFFFFFFFF),
    surfaceLevel1 = Color(0xFFFCFCFD),
    surfaceNeutral = Color(0xFFF7F7F8),
    surfaceSuccess = Color(0xFFD8F8E5),
    surfaceWarning = Color(0xFFFDEBE3),
    surfaceError = Color(0xFFFAD1D4),
    borderDefault = Color(0xFF8D8D95),
    borderDivider = Color(0xFFF2F2F3),
    borderSubtle = Color(0xFFE5E5E7),
    textPrimary = Color(0xFF121212),
    textSecondary = Color(0xFF313135),
    textTertiary = Color(0xFF4E4E55),
    textMuted = Color(0xFF67676F),
    textDisabled = Color(0xFF8D8D95),
    textOnColor = Color(0xFFFCFCFC),
    textSuccess = Color(0xFF014B21),
    textWarning = Color(0xFF742702),
    textError = Color(0xFF7B0F17),
    actionPrimaryDefault = Color(0xFF2B2A93),
    actionPrimaryPressed = Color(0xFF1E1E76),
    actionPrimaryDisabled = Color(0xFFE5E5E7),
    actionSecondaryDefault = Color(0xFFFFFFFF),
    actionSecondaryPressed = Color(0xFFE5E5E7),
    actionBorderSelected = Color(0xFF4E4E55),
    actionBorderDisabled = Color(0xFFC4C4C8)
)

internal val DarkColorPalette = POColors(
    surfaceBackground = Color(0xFF121421),
    surfaceLevel1 = Color(0xFF111322),
    surfaceNeutral = Color(0xFF181A2A),
    surfaceSuccess = Color(0xFF014B21),
    surfaceWarning = Color(0xFFA23807),
    surfaceError = Color(0xFFC0212B),
    borderDefault = Color(0xFF93949F),
    borderDivider = Color(0xFF212431),
    borderSubtle = Color(0xFF2B2D3A),
    textPrimary = Color(0xFFF8F8FB),
    textSecondary = Color(0xFFD4D4DD),
    textTertiary = Color(0xFFBABAC5),
    textMuted = Color(0xFF747581),
    textDisabled = Color(0xFF4D4F5B),
    textOnColor = Color(0xFFF8F8FB),
    textSuccess = Color(0xFFD8F8E5),
    textWarning = Color(0xFFFDEBE3),
    textError = Color(0xFFFBE9EB),
    actionPrimaryDefault = Color(0xFF6A64D8),
    actionPrimaryPressed = Color(0xFF4D49C5),
    actionPrimaryDisabled = Color(0xFF2B2D3A),
    actionSecondaryDefault = Color(0xFF121421),
    actionSecondaryPressed = Color(0xFF181A2A),
    actionBorderSelected = Color(0xFFBABAC5),
    actionBorderDisabled = Color(0xFF363945)
)

internal val LocalPOColors = staticCompositionLocalOf { LightColorPalette }
