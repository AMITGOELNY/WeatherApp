package com.ghn.weather.presentation.weather.viewmodel

import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.model.Location

data class WeatherState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val dailyForecast: List<DailyWeather> = emptyList(),
    val location: Location? = null,
    val error: String? = null,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.FAHRENHEIT
)