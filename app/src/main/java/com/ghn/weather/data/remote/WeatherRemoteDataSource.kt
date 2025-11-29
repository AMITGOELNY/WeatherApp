package com.ghn.weather.data.remote

import com.ghn.weather.data.dto.WeatherResponseDto
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(
    private val apiService: OpenMeteoApiService
) {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius"
    ): WeatherResponseDto {
        // Wind speed unit should match temperature unit convention:
        // Celsius -> km/h, Fahrenheit -> mph
        val windSpeedUnit = if (temperatureUnit == "fahrenheit") "mph" else "kmh"

        return apiService.getWeather(
            latitude = latitude,
            longitude = longitude,
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit
        )
    }
}