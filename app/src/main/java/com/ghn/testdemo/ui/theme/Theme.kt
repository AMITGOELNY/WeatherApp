package com.ghn.testdemo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════════════════
// ATMOSPHERIC WEATHER THEME
// Premium dark-mode-first design system
// ═══════════════════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = Aurora,
    onPrimary = Obsidian,
    primaryContainer = AuroraDark,
    onPrimaryContainer = TextPrimary,
    secondary = Celestial,
    onSecondary = Obsidian,
    secondaryContainer = Twilight,
    onSecondaryContainer = TextPrimary,
    tertiary = SolarFlare,
    onTertiary = Obsidian,
    tertiaryContainer = Dusk,
    onTertiaryContainer = TextPrimary,
    background = DeepSpace,
    onBackground = TextPrimary,
    surface = Twilight,
    onSurface = TextPrimary,
    surfaceVariant = Dusk,
    onSurfaceVariant = TextSecondary,
    error = TempHot,
    onError = Obsidian,
    outline = TextMuted,
    outlineVariant = GlassBorder,
    scrim = Obsidian,
)

@Composable
fun TestDemoTheme(
    darkTheme: Boolean = true, // Always dark for this premium weather app
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val weatherColors = WeatherColors()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = Obsidian.toArgb()
                window.navigationBarColor = Obsidian.toArgb()
            }
        }
    }

    CompositionLocalProvider(
        LocalWeatherColors provides weatherColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Convenient accessor for weather colors
object WeatherTheme {
    val colors: WeatherColors
        @Composable
        @ReadOnlyComposable
        get() = LocalWeatherColors.current
}
