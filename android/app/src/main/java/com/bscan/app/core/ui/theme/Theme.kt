package com.bscan.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class BscanExtendedColors(
    val borderSubtle: Color,
    val borderStrong: Color,
    val surfaceElevated: Color,
    val textMuted: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
)

private val DarkExtendedColors = BscanExtendedColors(
    borderSubtle = BscanBorderDark,
    borderStrong = BscanBorderStrongDark,
    surfaceElevated = BscanNavySurfaceElevated,
    textMuted = BscanTextMutedDark,
    success = BscanSuccessDark,
    successContainer = BscanSuccessContainerDark,
    warning = BscanWarningDark,
    warningContainer = BscanWarningContainerDark,
)

private val LightExtendedColors = BscanExtendedColors(
    borderSubtle = BscanBorderLight,
    borderStrong = Color(0xFFB6C6DB),
    surfaceElevated = BscanLightSurfaceElevated,
    textMuted = Color(0xFF64748B),
    success = Color(0xFF059669),
    successContainer = Color(0xFFD1FAE5),
    warning = Color(0xFFD97706),
    warningContainer = Color(0xFFFEF3C7),
)

val LocalBscanColors = staticCompositionLocalOf { DarkExtendedColors }

private val BscanDarkColorScheme = darkColorScheme(
    primary = BscanCyanAccent,
    onPrimary = BscanCyanOnAccent,
    primaryContainer = BscanCyanContainerDark,
    onPrimaryContainer = BscanCyanOnContainerDark,
    secondary = BscanTextSecondaryDark,
    onSecondary = BscanNavyBackground,
    secondaryContainer = BscanNavySurfaceElevated,
    onSecondaryContainer = BscanTextPrimaryDark,
    background = BscanNavyBackground,
    onBackground = BscanTextPrimaryDark,
    surface = BscanNavySurface,
    onSurface = BscanTextPrimaryDark,
    surfaceVariant = BscanNavySurfaceElevated,
    onSurfaceVariant = BscanTextSecondaryDark,
    outline = BscanBorderDark,
    outlineVariant = BscanBorderStrongDark,
    error = BscanErrorDark,
    onError = Color(0xFF28080D),
    errorContainer = BscanErrorContainerDark,
    onErrorContainer = BscanOnErrorContainerDark,
)

private val BscanLightColorScheme = lightColorScheme(
    primary = BscanCyanAccentLight,
    onPrimary = BscanCyanOnAccentLight,
    primaryContainer = BscanCyanContainerLight,
    onPrimaryContainer = BscanCyanOnContainerLight,
    secondary = BscanTextSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = BscanLightSurfaceElevated,
    onSecondaryContainer = BscanTextPrimaryLight,
    background = BscanLightBackground,
    onBackground = BscanTextPrimaryLight,
    surface = BscanLightSurface,
    onSurface = BscanTextPrimaryLight,
    surfaceVariant = BscanLightSurfaceElevated,
    onSurfaceVariant = BscanTextSecondaryLight,
    outline = BscanBorderLight,
    outlineVariant = Color(0xFFB6C6DB),
)

@Composable
fun BscanTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) BscanDarkColorScheme else BscanLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalBscanColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BscanTypography,
            shapes = BscanShapes,
            content = content,
        )
    }
}

object BscanThemeTokens {
    val extendedColors: BscanExtendedColors
        @Composable get() = LocalBscanColors.current
}
