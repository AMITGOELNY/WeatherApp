package com.ghn.weather.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.ghn.weather.R
import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.Location
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.presentation.weather.viewmodel.TemperatureUnit
import com.ghn.weather.ui.theme.*

@Composable
fun CurrentWeatherCard(
    currentWeather: CurrentWeather,
    location: Location?,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val theme = getWeatherTheme(currentWeather.weatherCode)

    GlowingGlassCard(
        modifier = modifier.fillMaxWidth(),
        theme = theme,
        cornerRadius = 32.dp,
        glowIntensity = 0.8f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location
            location?.let {
                val cityName = it.timezone
                    .replace("_", " ")
                    .split("/")
                    .lastOrNull() ?: stringResource(R.string.unknown_location)

                Text(
                    text = cityName.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 3.sp,
                    color = theme.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Weather condition
            Text(
                text = currentWeather.weatherCode.description,
                style = MaterialTheme.typography.titleMedium,
                color = theme.primaryColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Weather icon with glow
            WeatherIcon(
                weatherCode = currentWeather.weatherCode,
                size = 100.dp,
                animated = true,
                tint = theme.primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hero temperature
            Text(
                text = "${currentWeather.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Thin,
                    fontSize = 120.sp,
                    letterSpacing = (-6).sp
                ),
                color = theme.textPrimary
            )

            // Feels like
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.accentColor.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.feels_like, currentWeather.apparentTemperature.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.accentColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Weather metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherMetric(
                    icon = Icons.Default.WaterDrop,
                    value = "${currentWeather.humidity}%",
                    label = stringResource(R.string.humidity_label),
                    accentColor = RainBlue,
                    theme = theme
                )

                WeatherMetric(
                    icon = Icons.Default.Air,
                    value = "${currentWeather.windSpeed.toInt()}",
                    label = if (temperatureUnit == TemperatureUnit.CELSIUS) {
                        stringResource(R.string.wind_speed_kmh)
                    } else {
                        stringResource(R.string.wind_speed_mph)
                    },
                    accentColor = Aurora,
                    theme = theme
                )

                WeatherMetric(
                    icon = Icons.Default.Thermostat,
                    value = "${currentWeather.apparentTemperature.toInt()}°",
                    label = stringResource(R.string.feels_label),
                    accentColor = if (currentWeather.apparentTemperature > 25) TempHot else TempCool,
                    theme = theme
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sun times bar
            // TODO: Replace with actual sunrise/sunset data from API
            SunTimesBar(
                sunriseTime = "6:45 AM",
                sunsetTime = "7:32 PM",
                theme = theme
            )
        }
    }
}

@Composable
private fun WeatherMetric(
    icon: ImageVector,
    value: String,
    label: String,
    accentColor: Color,
    theme: AtmosphericTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = accentColor
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = theme.textSecondary
        )
    }
}

@Composable
private fun SunTimesBar(
    sunriseTime: String,
    sunsetTime: String,
    theme: AtmosphericTheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sunrise
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = stringResource(R.string.sunrise_label),
                modifier = Modifier.size(20.dp),
                tint = SunnyGold
            )
            Column {
                Text(
                    text = sunriseTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = theme.textPrimary
                )
                Text(
                    text = stringResource(R.string.sunrise_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.textSecondary
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(theme.textSecondary.copy(alpha = 0.2f))
        )

        // Sunset
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WbTwilight,
                contentDescription = stringResource(R.string.sunset_label),
                modifier = Modifier.size(20.dp),
                tint = SolarFlare
            )
            Column {
                Text(
                    text = sunsetTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = theme.textPrimary
                )
                Text(
                    text = stringResource(R.string.sunset_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.textSecondary
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREVIEW PARAMETER PROVIDER
// ═══════════════════════════════════════════════════════════════════════════════

private class WeatherCodePreviewProvider : PreviewParameterProvider<WeatherCode> {
    override val values: Sequence<WeatherCode> = sequenceOf(
        WeatherCode.CLEAR_SKY,
        WeatherCode.MAINLY_CLEAR,
        WeatherCode.PARTLY_CLOUDY,
        WeatherCode.OVERCAST,
        WeatherCode.FOG,
        WeatherCode.LIGHT_DRIZZLE,
        WeatherCode.LIGHT_RAIN,
        WeatherCode.HEAVY_RAIN,
        WeatherCode.LIGHT_SNOW,
        WeatherCode.HEAVY_SNOW,
        WeatherCode.THUNDERSTORM,
    )
}

private fun createPreviewWeather(weatherCode: WeatherCode) = CurrentWeather(
    time = "2024-01-15T12:00",
    temperature = when (weatherCode) {
        WeatherCode.CLEAR_SKY, WeatherCode.MAINLY_CLEAR -> 28.0
        WeatherCode.PARTLY_CLOUDY -> 22.0
        WeatherCode.OVERCAST -> 18.0
        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> 12.0
        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE -> 15.0
        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN -> 14.0
        WeatherCode.HEAVY_RAIN -> 13.0
        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW -> -2.0
        WeatherCode.HEAVY_SNOW -> -8.0
        WeatherCode.THUNDERSTORM -> 24.0
        WeatherCode.UNKNOWN -> 20.0
    },
    apparentTemperature = when (weatherCode) {
        WeatherCode.CLEAR_SKY, WeatherCode.MAINLY_CLEAR -> 30.0
        WeatherCode.PARTLY_CLOUDY -> 21.0
        WeatherCode.OVERCAST -> 16.0
        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> 10.0
        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE -> 13.0
        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN -> 12.0
        WeatherCode.HEAVY_RAIN -> 11.0
        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW -> -6.0
        WeatherCode.HEAVY_SNOW -> -14.0
        WeatherCode.THUNDERSTORM -> 26.0
        WeatherCode.UNKNOWN -> 19.0
    },
    humidity = when (weatherCode) {
        WeatherCode.CLEAR_SKY, WeatherCode.MAINLY_CLEAR -> 45
        WeatherCode.PARTLY_CLOUDY -> 55
        WeatherCode.OVERCAST -> 70
        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> 95
        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE -> 80
        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN -> 85
        WeatherCode.HEAVY_RAIN -> 92
        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW -> 75
        WeatherCode.HEAVY_SNOW -> 80
        WeatherCode.THUNDERSTORM -> 78
        WeatherCode.UNKNOWN -> 60
    },
    windSpeed = when (weatherCode) {
        WeatherCode.CLEAR_SKY, WeatherCode.MAINLY_CLEAR -> 8.0
        WeatherCode.PARTLY_CLOUDY -> 12.0
        WeatherCode.OVERCAST -> 15.0
        WeatherCode.FOG, WeatherCode.DEPOSITING_RIME_FOG -> 5.0
        WeatherCode.LIGHT_DRIZZLE, WeatherCode.MODERATE_DRIZZLE, WeatherCode.DENSE_DRIZZLE -> 18.0
        WeatherCode.LIGHT_RAIN, WeatherCode.MODERATE_RAIN -> 22.0
        WeatherCode.HEAVY_RAIN -> 35.0
        WeatherCode.LIGHT_SNOW, WeatherCode.MODERATE_SNOW -> 20.0
        WeatherCode.HEAVY_SNOW -> 28.0
        WeatherCode.THUNDERSTORM -> 45.0
        WeatherCode.UNKNOWN -> 10.0
    },
    weatherCode = weatherCode
)

private val previewLocation = Location(
    latitude = 25.7617,
    longitude = -80.1918,
    timezone = "America/New_York"
)

// ═══════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF0A0A1A)
@Composable
private fun CurrentWeatherCardPreview(
    @PreviewParameter(WeatherCodePreviewProvider::class) weatherCode: WeatherCode
) {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(weatherCode).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(weatherCode),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Clear Sky", showBackground = true, backgroundColor = 0xFF0A1628)
@Composable
private fun PreviewClearSky() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.CLEAR_SKY).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.CLEAR_SKY),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Partly Cloudy", showBackground = true, backgroundColor = 0xFF0D1520)
@Composable
private fun PreviewPartlyCloudy() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.PARTLY_CLOUDY).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.PARTLY_CLOUDY),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Rainy", showBackground = true, backgroundColor = 0xFF081018)
@Composable
private fun PreviewRainy() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.MODERATE_RAIN).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.MODERATE_RAIN),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Heavy Rain", showBackground = true, backgroundColor = 0xFF060A10)
@Composable
private fun PreviewHeavyRain() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.HEAVY_RAIN).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.HEAVY_RAIN),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Snow", showBackground = true, backgroundColor = 0xFF101820)
@Composable
private fun PreviewSnow() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.MODERATE_SNOW).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.MODERATE_SNOW),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Thunderstorm", showBackground = true, backgroundColor = 0xFF080610)
@Composable
private fun PreviewThunderstorm() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.THUNDERSTORM).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.THUNDERSTORM),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Fog", showBackground = true, backgroundColor = 0xFF12181E)
@Composable
private fun PreviewFog() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.FOG).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = createPreviewWeather(WeatherCode.FOG),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Preview(name = "Fahrenheit", showBackground = true, backgroundColor = 0xFF0A1628)
@Composable
private fun PreviewFahrenheit() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .background(getWeatherTheme(WeatherCode.CLEAR_SKY).backgroundGradient)
                .padding(16.dp)
        ) {
            CurrentWeatherCard(
                currentWeather = CurrentWeather(
                    time = "2024-01-15T12:00",
                    temperature = 82.0,
                    apparentTemperature = 86.0,
                    humidity = 45,
                    windSpeed = 5.0,
                    weatherCode = WeatherCode.CLEAR_SKY
                ),
                location = previewLocation,
                temperatureUnit = TemperatureUnit.FAHRENHEIT
            )
        }
    }
}
