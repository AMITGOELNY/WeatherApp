package com.ghn.testdemo.domain.model

data class Weather(
    val location: Location,
    val current: CurrentWeather,
    val hourlyForecast: List<HourlyWeather>,
    val dailyForecast: List<DailyWeather>
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)

data class CurrentWeather(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: WeatherCode
)

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: WeatherCode
)

data class DailyWeather(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val weatherCode: WeatherCode
)

enum class WeatherCode(val code: Int, val description: String) {
    CLEAR_SKY(0, "Clear sky"),
    MAINLY_CLEAR(1, "Mainly clear"),
    PARTLY_CLOUDY(2, "Partly cloudy"),
    OVERCAST(3, "Overcast"),
    FOG(45, "Fog"),
    DEPOSITING_RIME_FOG(48, "Depositing rime fog"),
    LIGHT_DRIZZLE(51, "Light drizzle"),
    MODERATE_DRIZZLE(53, "Moderate drizzle"),
    DENSE_DRIZZLE(55, "Dense drizzle"),
    LIGHT_RAIN(61, "Light rain"),
    MODERATE_RAIN(63, "Moderate rain"),
    HEAVY_RAIN(65, "Heavy rain"),
    LIGHT_SNOW(71, "Light snow"),
    MODERATE_SNOW(73, "Moderate snow"),
    HEAVY_SNOW(75, "Heavy snow"),
    THUNDERSTORM(95, "Thunderstorm"),
    UNKNOWN(-1, "Unknown");

    companion object {
        fun fromCode(code: Int): WeatherCode {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}