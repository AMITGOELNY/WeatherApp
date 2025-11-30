package com.ghn.weather.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("current")
    val current: CurrentWeatherDto,
    @SerialName("hourly")
    val hourly: HourlyWeatherDto,
    @SerialName("daily")
    val daily: DailyWeatherDto
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("time")
    val time: String,
    @SerialName("temperature_2m")
    val temperature: Double,
    @SerialName("apparent_temperature")
    val apparentTemperature: Double,
    @SerialName("relative_humidity_2m")
    val humidity: Int,
    @SerialName("wind_speed_10m")
    val windSpeed: Double,
    @SerialName("weather_code")
    val weatherCode: Int
)

@Serializable
data class HourlyWeatherDto(
    @SerialName("time")
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature: List<Double>,
    @SerialName("relative_humidity_2m")
    val humidity: List<Int>,
    @SerialName("wind_speed_10m")
    val windSpeed: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>
)

@Serializable
data class DailyWeatherDto(
    @SerialName("time")
    val time: List<String>,
    @SerialName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("sunrise")
    val sunrise: List<String>,
    @SerialName("sunset")
    val sunset: List<String>
)