package com.ghn.weather.presentation.weather

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.ui.theme.*

// ═══════════════════════════════════════════════════════════════════════════════
// ATMOSPHERIC WEATHER THEMES
// Condition-specific color schemes for immersive weather visualization
// ═══════════════════════════════════════════════════════════════════════════════

data class AtmosphericTheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val backgroundGradient: Brush,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glowColor: Color,
    val ambientOrbColor: Color,
)

fun getWeatherTheme(weatherCode: WeatherCode): AtmosphericTheme =
    when (weatherCode) {
        WeatherCode.CLEAR_SKY -> AtmosphericTheme(
            primaryColor = SunnyGold,
            secondaryColor = Color(0xFFFFB347),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A1628),
                    Color(0xFF1A2744),
                    Color(0xFF2D3E5C),
                )
            ),
            cardBackground = Color(0xFF1A2744).copy(alpha = 0.6f),
            cardBorder = SunnyGold.copy(alpha = 0.15f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = SunnyGold.copy(alpha = 0.3f),
            ambientOrbColor = SunnyGold,
        )

        WeatherCode.MAINLY_CLEAR -> AtmosphericTheme(
            primaryColor = Color(0xFFF4D03F),
            secondaryColor = Color(0xFFE8B923),
            accentColor = AuroraLight,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0B1829),
                    Color(0xFF162D50),
                    Color(0xFF234369),
                )
            ),
            cardBackground = Color(0xFF162D50).copy(alpha = 0.6f),
            cardBorder = Color(0xFFF4D03F).copy(alpha = 0.12f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Color(0xFFF4D03F).copy(alpha = 0.25f),
            ambientOrbColor = Color(0xFFF4D03F),
        )

        WeatherCode.PARTLY_CLOUDY -> AtmosphericTheme(
            primaryColor = CloudSilver,
            secondaryColor = Color(0xFF7E8C9A),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1520),
                    Color(0xFF1B2838),
                    Color(0xFF2C3E50),
                )
            ),
            cardBackground = Color(0xFF1B2838).copy(alpha = 0.65f),
            cardBorder = CloudSilver.copy(alpha = 0.12f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = CloudSilver.copy(alpha = 0.15f),
            ambientOrbColor = CloudSilver,
        )

        WeatherCode.OVERCAST -> AtmosphericTheme(
            primaryColor = Color(0xFF8899A8),
            secondaryColor = Color(0xFF6B7C8A),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0E1318),
                    Color(0xFF1A2128),
                    Color(0xFF283038),
                )
            ),
            cardBackground = Color(0xFF1A2128).copy(alpha = 0.7f),
            cardBorder = Color(0xFF8899A8).copy(alpha = 0.1f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Color(0xFF8899A8).copy(alpha = 0.1f),
            ambientOrbColor = Color(0xFF8899A8),
        )

        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> AtmosphericTheme(
            primaryColor = FogMist,
            secondaryColor = Color(0xFFB8C6D4),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF12181E),
                    Color(0xFF1E2830),
                    Color(0xFF2A3642),
                )
            ),
            cardBackground = Color(0xFF1E2830).copy(alpha = 0.75f),
            cardBorder = FogMist.copy(alpha = 0.08f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = FogMist.copy(alpha = 0.08f),
            ambientOrbColor = FogMist,
        )

        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE -> AtmosphericTheme(
            primaryColor = RainBlue,
            secondaryColor = Color(0xFF3498DB),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A1520),
                    Color(0xFF152535),
                    Color(0xFF1E3448),
                )
            ),
            cardBackground = Color(0xFF152535).copy(alpha = 0.65f),
            cardBorder = RainBlue.copy(alpha = 0.12f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = RainBlue.copy(alpha = 0.2f),
            ambientOrbColor = RainBlue,
        )

        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN -> AtmosphericTheme(
            primaryColor = Color(0xFF3498DB),
            secondaryColor = Color(0xFF2980B9),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF081018),
                    Color(0xFF0F1E2C),
                    Color(0xFF182D42),
                )
            ),
            cardBackground = Color(0xFF0F1E2C).copy(alpha = 0.7f),
            cardBorder = Color(0xFF3498DB).copy(alpha = 0.15f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Color(0xFF3498DB).copy(alpha = 0.25f),
            ambientOrbColor = Color(0xFF3498DB),
        )

        WeatherCode.HEAVY_RAIN -> AtmosphericTheme(
            primaryColor = Color(0xFF2471A3),
            secondaryColor = Color(0xFF1A5276),
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF060A10),
                    Color(0xFF0C1520),
                    Color(0xFF142030),
                )
            ),
            cardBackground = Color(0xFF0C1520).copy(alpha = 0.75f),
            cardBorder = Color(0xFF2471A3).copy(alpha = 0.18f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Color(0xFF2471A3).copy(alpha = 0.3f),
            ambientOrbColor = Color(0xFF2471A3),
        )

        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW -> AtmosphericTheme(
            primaryColor = SnowWhite,
            secondaryColor = Color(0xFFD5E8F0),
            accentColor = TempCool,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF101820),
                    Color(0xFF1A2832),
                    Color(0xFF243848),
                )
            ),
            cardBackground = Color(0xFF1A2832).copy(alpha = 0.6f),
            cardBorder = SnowWhite.copy(alpha = 0.12f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = SnowWhite.copy(alpha = 0.15f),
            ambientOrbColor = SnowWhite,
        )

        WeatherCode.HEAVY_SNOW -> AtmosphericTheme(
            primaryColor = Color(0xFFF8FCFF),
            secondaryColor = SnowWhite,
            accentColor = TempCool,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0E151C),
                    Color(0xFF182430),
                    Color(0xFF223344),
                )
            ),
            cardBackground = Color(0xFF182430).copy(alpha = 0.65f),
            cardBorder = Color(0xFFF8FCFF).copy(alpha = 0.15f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Color(0xFFF8FCFF).copy(alpha = 0.2f),
            ambientOrbColor = Color(0xFFF8FCFF),
        )

        WeatherCode.THUNDERSTORM -> AtmosphericTheme(
            primaryColor = StormPurple,
            secondaryColor = Color(0xFF8E44AD),
            accentColor = SolarFlare,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF080610),
                    Color(0xFF120E1C),
                    Color(0xFF1C1428),
                )
            ),
            cardBackground = Color(0xFF120E1C).copy(alpha = 0.7f),
            cardBorder = StormPurple.copy(alpha = 0.2f),
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = StormPurple.copy(alpha = 0.35f),
            ambientOrbColor = StormPurple,
        )

        WeatherCode.UNKNOWN -> AtmosphericTheme(
            primaryColor = CloudSilver,
            secondaryColor = TextSecondary,
            accentColor = Aurora,
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(
                    Obsidian,
                    DeepSpace,
                    Midnight,
                )
            ),
            cardBackground = Twilight.copy(alpha = 0.6f),
            cardBorder = GlassBorder,
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            glowColor = Aurora.copy(alpha = 0.1f),
            ambientOrbColor = Aurora,
        )
    }

// ═══════════════════════════════════════════════════════════════════════════════
// GLASSMORPHIC CARD COMPONENTS
// Premium glass-effect cards with subtle borders and backdrop effects
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    theme: AtmosphericTheme = getWeatherTheme(WeatherCode.UNKNOWN),
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.cardBorder,
                        theme.cardBorder.copy(alpha = 0.05f),
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = theme.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
fun GlowingGlassCard(
    modifier: Modifier = Modifier,
    theme: AtmosphericTheme = getWeatherTheme(WeatherCode.UNKNOWN),
    cornerRadius: Dp = 24.dp,
    glowIntensity: Float = 1f,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        // Glow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .blur(20.dp)
                .alpha(0.3f * glowIntensity)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            theme.glowColor,
                            Color.Transparent,
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        // Main card
        GlassCard(
            theme = theme,
            cornerRadius = cornerRadius,
            content = content
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// AMBIENT BACKGROUND ANIMATION
// Floating orbs that create atmospheric depth
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    theme: AtmosphericTheme = getWeatherTheme(WeatherCode.UNKNOWN),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha1"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Primary ambient orb - top right
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 120.dp + offset1.dp, y = (-80).dp)
                .alpha(alpha1)
                .blur(80.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                theme.ambientOrbColor,
                                Color.Transparent
                            )
                        )
                    )
                }
        )

        // Secondary ambient orb - bottom left
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-60).dp + offset2.dp, y = 400.dp)
                .alpha(alpha1 * 0.6f)
                .blur(60.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                theme.primaryColor.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
                }
        )

        // Tertiary accent orb
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 200.dp - offset1.dp, y = 600.dp + offset2.dp)
                .alpha(alpha1 * 0.4f)
                .blur(50.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                theme.accentColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
                }
        )
    }
}
