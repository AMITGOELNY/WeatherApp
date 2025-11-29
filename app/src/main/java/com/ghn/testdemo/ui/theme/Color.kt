package com.ghn.testdemo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
// ATMOSPHERIC WEATHER PALETTE
// A premium dark-mode-first color system inspired by night skies and aurora
// ═══════════════════════════════════════════════════════════════════════════════

// Core Surfaces (Dark Theme)
val Obsidian = Color(0xFF060610)           // Deepest background - almost black with blue tint
val DeepSpace = Color(0xFF0A0A1A)          // Primary background
val Midnight = Color(0xFF0F1022)           // Elevated surfaces
val Twilight = Color(0xFF161835)           // Cards and containers
val Dusk = Color(0xFF1E2148)               // Highest elevation / active states

// Accent System - Aurora inspired
val Aurora = Color(0xFF00E5CC)             // Primary accent - ethereal teal
val AuroraLight = Color(0xFF5FFFEA)        // Hover/highlight
val AuroraDark = Color(0xFF00B8A3)         // Pressed state
val AuroraGlow = Color(0x4000E5CC)         // Glow effect

// Secondary Accents
val Celestial = Color(0xFF7B68EE)          // Purple accent for special states
val SolarFlare = Color(0xFFFF6B35)         // Warm accent for warnings/highs
val MoonGlow = Color(0xFFF0F4FF)           // Cool white for text highlights
val Nebula = Color(0xFF9B59B6)             // Purple for thunderstorms

// Text Hierarchy
val TextPrimary = Color(0xFFF5F7FA)        // High emphasis - near white
val TextSecondary = Color(0xFFB8C4D6)      // Medium emphasis
val TextTertiary = Color(0xFF6B7A94)       // Low emphasis
val TextMuted = Color(0xFF3D4A63)          // Disabled/subtle

// Weather Semantic Colors
val TempHot = Color(0xFFFF6B6B)            // High temperatures
val TempWarm = Color(0xFFFFB86C)           // Warm temperatures
val TempMild = Color(0xFF50FA7B)           // Mild temperatures
val TempCool = Color(0xFF8BE9FD)           // Cool temperatures
val TempCold = Color(0xFF6272A4)           // Cold temperatures

// Condition Colors
val SunnyGold = Color(0xFFFFD93D)          // Clear/Sunny
val CloudSilver = Color(0xFF9CA8B8)        // Cloudy
val RainBlue = Color(0xFF4DA6FF)           // Rain
val SnowWhite = Color(0xFFE8F4F8)          // Snow
val StormPurple = Color(0xFF9B59B6)        // Thunderstorm
val FogMist = Color(0xFFC9D6E3)            // Fog

// Surface Overlays
val GlassWhite = Color(0x15FFFFFF)         // Glassmorphic white
val GlassBorder = Color(0x25FFFFFF)        // Glass border
val CardGlow = Color(0x0800E5CC)           // Subtle card glow

// Extended Colors Class for CompositionLocal
@Immutable
data class WeatherColors(
    val background: Color = DeepSpace,
    val backgroundGradientStart: Color = Obsidian,
    val backgroundGradientEnd: Color = Midnight,
    val surface: Color = Twilight,
    val surfaceElevated: Color = Dusk,
    val accent: Color = Aurora,
    val accentLight: Color = AuroraLight,
    val accentGlow: Color = AuroraGlow,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
    val tempHot: Color = TempHot,
    val tempCold: Color = TempCool,
    val glassWhite: Color = GlassWhite,
    val glassBorder: Color = GlassBorder,
)

val LocalWeatherColors = staticCompositionLocalOf { WeatherColors() }
