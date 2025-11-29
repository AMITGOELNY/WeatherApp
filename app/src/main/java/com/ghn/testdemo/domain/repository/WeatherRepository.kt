package com.ghn.testdemo.domain.repository

import com.ghn.testdemo.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius"
    ): Result<Weather>
}