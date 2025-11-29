package com.ghn.weather.presentation.weather

sealed class WeatherIntent {
    data class LoadWeather(val latitude: Double, val longitude: Double) : WeatherIntent()
    data object RefreshWeather : WeatherIntent()
    data object Retry : WeatherIntent()
    data object ToggleTemperatureUnit : WeatherIntent()
}

enum class TemperatureUnit(val apiParam: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F")
}