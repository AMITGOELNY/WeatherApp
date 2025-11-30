package com.ghn.weather.presentation.weather.viewmodel

sealed class WeatherEffect {
    data class ShowError(val message: String) : WeatherEffect()
    data object ShowLocationPermissionRequest : WeatherEffect()
}