package com.fiverules.common.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object FrTheme {
    val colors: FrColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFrColors.current

    val typography: FrTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalFrTypography.current
}

private val LightColorScheme = lightColorScheme(
    primary = FrColorsLight.primary,
    onPrimary = FrColorsLight.onPrimary,
    primaryContainer = FrColorsLight.primarySoft,
    onPrimaryContainer = FrColorsLight.primary,
    secondary = FrColorsLight.textMuted,
    background = FrColorsLight.background,
    surface = FrColorsLight.surface,
    onSurface = FrColorsLight.textPrimary,
    onBackground = FrColorsLight.textPrimary,
    error = FrColorsLight.error,
    onError = FrColorsLight.onPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = FrColorsDark.primary,
    onPrimary = FrColorsDark.onPrimary,
    primaryContainer = FrColorsDark.primarySoft,
    onPrimaryContainer = FrColorsDark.primary,
    secondary = FrColorsDark.textMuted,
    background = FrColorsDark.background,
    surface = FrColorsDark.surface,
    onSurface = FrColorsDark.textPrimary,
    onBackground = FrColorsDark.textPrimary,
    error = FrColorsDark.error,
    onError = FrColorsDark.onPrimary,
)

@Composable
fun FiveRulesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) FrColorsDark else FrColorsLight
    CompositionLocalProvider(
        LocalFrColors provides colors,
        LocalFrTypography provides FrTypography(),
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
