package com.ghn.weather.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ghn.weather.R

// ═══════════════════════════════════════════════════════════════════════════════
// OUTFIT FONT FAMILY
// Modern geometric sans-serif - beautiful thin weights for big numbers
// ═══════════════════════════════════════════════════════════════════════════════

val OutfitFontFamily = FontFamily(
    Font(R.font.outfit_thin, FontWeight.Thin),
    Font(R.font.outfit_extralight, FontWeight.ExtraLight),
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold),
)

// ═══════════════════════════════════════════════════════════════════════════════
// WEATHER APP TYPOGRAPHY
// Clean, modern typography scale optimized for weather data display
// ═══════════════════════════════════════════════════════════════════════════════

val Typography = Typography(
    // Hero temperature display - massive, ultra-thin
    displayLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Thin,
        fontSize = 96.sp,
        letterSpacing = (-4).sp,
        lineHeight = 104.sp
    ),

    // Large temperature - for cards
    displayMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 64.sp,
        letterSpacing = (-2).sp,
        lineHeight = 72.sp
    ),

    // Medium numbers
    displaySmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        letterSpacing = (-1).sp,
        lineHeight = 52.sp
    ),

    // Section headers
    headlineLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 36.sp
    ),

    // Card titles
    headlineMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
        lineHeight = 28.sp
    ),

    // Subsection headers
    headlineSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),

    // Large titles
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 28.sp
    ),

    // Medium titles
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        lineHeight = 24.sp
    ),

    // Small titles
    titleSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp
    ),

    // Body text - primary reading
    bodyLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 24.sp
    ),

    // Body text - secondary
    bodyMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp
    ),

    // Body text - tertiary
    bodySmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp
    ),

    // Labels - large buttons
    labelLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 20.sp
    ),

    // Labels - medium
    labelMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp
    ),

    // Labels - small captions
    labelSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 14.sp
    )
)

// ═══════════════════════════════════════════════════════════════════════════════
// CUSTOM TYPOGRAPHY EXTENSIONS
// Weather-specific text styles
// ═══════════════════════════════════════════════════════════════════════════════

val Typography.heroTemperature: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Thin,
        fontSize = 120.sp,
        letterSpacing = (-6).sp,
        lineHeight = 120.sp
    )

val Typography.cardTemperature: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        letterSpacing = (-1).sp,
        lineHeight = 36.sp
    )

val Typography.metricValue: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    )

val Typography.metricLabel: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        lineHeight = 14.sp
    )
