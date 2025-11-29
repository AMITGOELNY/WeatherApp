package com.ghn.weather.domain.repository

import com.ghn.weather.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius"
    ): Result<Weather>
}