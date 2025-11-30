package com.ghn.weather.data.remote

import com.ghn.weather.data.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("temperature_unit") temperatureUnit: String = "fahrenheit",
        @Query("wind_speed_unit") windSpeedUnit: String = "mph",
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weather_code,sunrise,sunset",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponseDto
}