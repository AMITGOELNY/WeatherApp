package com.ghn.weather.presentation.weather

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ═══════════════════════════════════════════════════════════════════════════════
// WEATHER ICON COMPONENT
// Animated icons with optional glow effect for dramatic weather conditions
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun WeatherIcon(
    weatherCode: WeatherCode,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    animated: Boolean = true,
    tint: Color? = null
) {
    val theme = getWeatherTheme(weatherCode)

    val icon = when (weatherCode) {
        WeatherCode.CLEAR_SKY, WeatherCode.MAINLY_CLEAR -> Icons.Outlined.WbSunny
        WeatherCode.PARTLY_CLOUDY -> Icons.Outlined.WbCloudy
        WeatherCode.OVERCAST -> Icons.Outlined.Cloud
        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> Icons.Outlined.Cloud
        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE,
        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN, WeatherCode.HEAVY_RAIN -> Icons.Outlined.WaterDrop
        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW, WeatherCode.HEAVY_SNOW -> Icons.Outlined.AcUnit
        WeatherCode.THUNDERSTORM -> Icons.Outlined.Thunderstorm
        WeatherCode.UNKNOWN -> Icons.Outlined.Cloud
    }

    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (animated) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val hasGlow = weatherCode in listOf(
        WeatherCode.CLEAR_SKY,
        WeatherCode.MAINLY_CLEAR,
        WeatherCode.THUNDERSTORM
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for special conditions
        if (hasGlow && animated) {
            Box(
                modifier = Modifier
                    .size(size * 1.6f)
                    .scale(pulseScale)
                    .alpha(glowAlpha)
                    .blur(size / 3)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    theme.glowColor,
                                    Color.Transparent
                                )
                            )
                        )
                    }
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = weatherCode.description,
            modifier = Modifier
                .size(size)
                .scale(if (animated) pulseScale else 1f),
            tint = tint ?: theme.primaryColor
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TEMPERATURE UNIT TOGGLE
// Minimal pill-shaped toggle with smooth animations
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TemperatureUnitToggle(
    currentUnit: TemperatureUnit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "toggleScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Aurora.copy(alpha = 0.2f),
                        Aurora.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Aurora.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (currentUnit == TemperatureUnit.CELSIUS) "°C" else "°F",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Aurora
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HOURLY FORECAST ITEM
// Compact horizontal card for hourly weather data
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun HourlyForecastItem(
    hourlyWeather: HourlyWeather,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val theme = getWeatherTheme(hourlyWeather.weatherCode)

    GlassCard(
        modifier = modifier.width(90.dp),
        theme = theme,
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Time
            Text(
                text = formatHour(hourlyWeather.time),
                style = MaterialTheme.typography.labelMedium,
                color = theme.textSecondary
            )

            // Weather icon
            WeatherIcon(
                weatherCode = hourlyWeather.weatherCode,
                size = 32.dp,
                animated = false,
                tint = theme.primaryColor
            )

            // Temperature
            Text(
                text = "${hourlyWeather.temperature.toInt()}°",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
            )

            // Humidity indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = RainBlue.copy(alpha = 0.7f)
                )
                Text(
                    text = "${hourlyWeather.humidity}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.textSecondary
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DAILY FORECAST ITEM
// Elegant card showing day's weather summary
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun DailyForecastItem(
    dailyWeather: DailyWeather,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val theme = getWeatherTheme(dailyWeather.weatherCode)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        theme = theme,
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day name
            Text(
                text = formatDayName(dailyWeather.date),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary,
                modifier = Modifier.width(100.dp)
            )

            // Weather icon and condition
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                WeatherIcon(
                    weatherCode = dailyWeather.weatherCode,
                    size = 28.dp,
                    animated = false,
                    tint = theme.primaryColor
                )
                Text(
                    text = dailyWeather.weatherCode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.textSecondary,
                    maxLines = 1
                )
            }

            // Temperature range
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${dailyWeather.temperatureMax.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TempHot.copy(alpha = 0.9f)
                )

                // Temperature range bar
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(TempCool, TempHot)
                            )
                        )
                )

                Text(
                    text = "${dailyWeather.temperatureMin.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TempCool.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// Elegant section titles with accent line
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Aurora
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UTILITY FUNCTIONS
// ═══════════════════════════════════════════════════════════════════════════════

private fun formatHour(timeString: String): String =
    try {
        val dateTime = LocalDateTime.parse(timeString)
        dateTime.format(DateTimeFormatter.ofPattern("ha")).lowercase()
    } catch (e: Exception) {
        timeString
    }

private fun formatDayName(dateString: String): String =
    try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        when (date) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> date.format(DateTimeFormatter.ofPattern("EEEE"))
        }
    } catch (e: Exception) {
        dateString
    }

private fun formatTime(timeString: String): String =
    try {
        val dateTime = LocalDateTime.parse(timeString)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    } catch (e: Exception) {
        timeString
    }

private fun formatDate(dateString: String): String =
    try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("EEE, MMM dd"))
    } catch (e: Exception) {
        dateString
    }

private fun formatDateShort(dateString: String): String =
    try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("EEE"))
    } catch (e: Exception) {
        dateString
    }
