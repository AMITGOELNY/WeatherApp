package com.ghn.testdemo.presentation.weather

sealed class WeatherEffect {
    data class ShowError(val message: String) : WeatherEffect()
    data object ShowLocationPermissionRequest : WeatherEffect()
}