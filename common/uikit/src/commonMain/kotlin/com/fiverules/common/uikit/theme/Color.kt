package com.fiverules.common.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Figma color styles from 5rules (numeric tokens 1–10).
 * Light: 1=#000000 2=#FFFFFF 3=#5D5FEF 4=#F8F8FB 5=#9393BF 6=#EAEAFF 7=#EF5DA8 8=#FFFFFF
 * Dark:  1=#FFFFFF 2=#000000 3=#5D5FEF 4=#282828 5=#9B9BC1 6=#404047 7=#EF5DA8 8=#FFFFFF
 */
@Immutable
data class FrColors(
    val textPrimary: Color,
    val textMuted: Color,
    val primary: Color,
    val primarySoft: Color,
    val like: Color,
    val background: Color,
    val surface: Color,
    val bar: Color,
    val onPrimary: Color,
    val actionButton: Color,
    val overlay: Color,
    val error: Color,
    val shadow: Color,
)

val FrColorsLight = FrColors(
    textPrimary = Color(0xFF000000),
    textMuted = Color(0xFF9393BF),
    primary = Color(0xFF5D5FEF),
    primarySoft = Color(0xFFEAEAFF),
    like = Color(0xFFEF5DA8),
    background = Color(0xFFF8F8FB),
    surface = Color(0xFFFFFFFF),
    bar = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    actionButton = Color(0xB3FFFFFF),
    overlay = Color(0x80000000),
    error = Color(0xFFEF5DA8),
    shadow = Color(0x1A8890AA),
)

val FrColorsDark = FrColors(
    textPrimary = Color(0xFFFFFFFF),
    textMuted = Color(0xFF9B9BC1),
    primary = Color(0xFF5D5FEF),
    primarySoft = Color(0xFF404047),
    like = Color(0xFFEF5DA8),
    background = Color(0xFF282828),
    surface = Color(0xFF000000),
    bar = Color(0xFF404047),
    onPrimary = Color(0xFFFFFFFF),
    actionButton = Color(0xB3000000),
    overlay = Color(0x99000000),
    error = Color(0xFFEF5DA8),
    shadow = Color(0x4D21232B),
)

val LocalFrColors = staticCompositionLocalOf { FrColorsLight }

@Deprecated("Use FrColorsLight / FrTheme.colors", replaceWith = ReplaceWith("FrColorsLight.primary"))
val PrimaryLight = FrColorsLight.primary
@Deprecated("Use FrTheme.colors")
val OnPrimaryLight = FrColorsLight.onPrimary
@Deprecated("Use FrTheme.colors")
val PrimaryContainerLight = FrColorsLight.primarySoft
@Deprecated("Use FrTheme.colors")
val OnPrimaryContainerLight = FrColorsLight.primary
@Deprecated("Use FrTheme.colors")
val SecondaryLight = FrColorsLight.textMuted
@Deprecated("Use FrTheme.colors")
val BackgroundLight = FrColorsLight.background
@Deprecated("Use FrTheme.colors")
val SurfaceLight = FrColorsLight.surface
@Deprecated("Use FrTheme.colors")
val OnSurfaceLight = FrColorsLight.textPrimary
@Deprecated("Use FrTheme.colors")
val ErrorLight = FrColorsLight.error

@Deprecated("Use FrTheme.colors")
val PrimaryDark = FrColorsDark.primary
@Deprecated("Use FrTheme.colors")
val OnPrimaryDark = FrColorsDark.onPrimary
@Deprecated("Use FrTheme.colors")
val PrimaryContainerDark = FrColorsDark.primarySoft
@Deprecated("Use FrTheme.colors")
val OnPrimaryContainerDark = FrColorsDark.primarySoft
@Deprecated("Use FrTheme.colors")
val SecondaryDark = FrColorsDark.textMuted
@Deprecated("Use FrTheme.colors")
val BackgroundDark = FrColorsDark.background
@Deprecated("Use FrTheme.colors")
val SurfaceDark = FrColorsDark.surface
@Deprecated("Use FrTheme.colors")
val OnSurfaceDark = FrColorsDark.textPrimary
@Deprecated("Use FrTheme.colors")
val ErrorDark = FrColorsDark.error
