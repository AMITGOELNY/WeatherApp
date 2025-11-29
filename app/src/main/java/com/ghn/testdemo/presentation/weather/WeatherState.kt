package com.ghn.testdemo.presentation.weather

import com.ghn.testdemo.domain.model.CurrentWeather
import com.ghn.testdemo.domain.model.DailyWeather
import com.ghn.testdemo.domain.model.HourlyWeather
import com.ghn.testdemo.domain.model.Location

data class WeatherState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val dailyForecast: List<DailyWeather> = emptyList(),
    val location: Location? = null,
    val error: String? = null,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)