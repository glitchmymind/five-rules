package com.fiverules.common.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Figma text styles:
 * Title/Normal — Georgia 24
 * Large / Bold — SF Compact Display Bold 18
 * Normal / Medium — SF Compact Display Semibold 14
 * Normal/Normal — SF Compact Display Regular 14
 * Small / Bold — SF Compact Display Bold 12
 * Small / Normal — SF Compact Display Regular 12
 *
 * SF Compact Display is not bundled; SansSerif maps to SF on iOS and a grotesque on other targets.
 */
@Immutable
data class FrTypography(
    val titleNormal: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
    ),
    val largeBold: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 18.sp,
    ),
    val normalMedium: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 14.sp,
    ),
    val normalNormal: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 14.sp,
    ),
    val smallBold: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 12.sp,
    ),
    val smallNormal: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp,
    ),
)

val LocalFrTypography = staticCompositionLocalOf { FrTypography() }

val AppTypography = Typography(
    headlineLarge = FrTypography().titleNormal.copy(fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = FrTypography().titleNormal,
    titleLarge = FrTypography().largeBold,
    bodyLarge = FrTypography().normalNormal.copy(lineHeight = 20.sp),
    bodyMedium = FrTypography().normalNormal,
    labelLarge = FrTypography().normalMedium,
    labelSmall = FrTypography().smallNormal,
)
