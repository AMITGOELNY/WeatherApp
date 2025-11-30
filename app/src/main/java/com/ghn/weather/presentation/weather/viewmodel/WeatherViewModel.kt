package com.ghn.weather.presentation.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghn.weather.domain.usecase.GetCurrentWeatherUseCase
import com.ghn.weather.domain.usecase.GetWeatherForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _effect = Channel<WeatherEffect>()
    val effect = _effect.receiveAsFlow()

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    fun handleIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.LoadWeather -> loadWeather(intent.latitude, intent.longitude)
            is WeatherIntent.RefreshWeather -> refreshWeather()
            is WeatherIntent.Retry -> retry()
            is WeatherIntent.ToggleTemperatureUnit -> toggleTemperatureUnit()
        }
    }

    private fun loadWeather(latitude: Double, longitude: Double) {
        lastLatitude = latitude
        lastLongitude = longitude

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val tempUnit = _state.value.temperatureUnit.apiParam
            val currentWeatherResult = getCurrentWeatherUseCase(latitude, longitude, tempUnit)
            val forecastResult = getWeatherForecastUseCase(latitude, longitude, tempUnit)

            currentWeatherResult.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            currentWeather = result.current,
                            location = result.location,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                    _effect.send(WeatherEffect.ShowError(error.message ?: "Unknown error occurred"))
                }
            )

            forecastResult.fold(
                onSuccess = { forecast ->
                    _state.update {
                        it.copy(
                            hourlyForecast = forecast.hourlyForecast,
                            dailyForecast = forecast.dailyForecast
                        )
                    }
                },
                onFailure = { error ->
                    _effect.send(WeatherEffect.ShowError("Failed to load forecast: ${error.message}"))
                }
            )
        }
    }

    private fun refreshWeather() {
        val lat = lastLatitude
        val lon = lastLongitude

        if (lat != null && lon != null) {
            viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true, error = null) }

                val tempUnit = _state.value.temperatureUnit.apiParam
                val currentWeatherResult = getCurrentWeatherUseCase(lat, lon, tempUnit)
                val forecastResult = getWeatherForecastUseCase(lat, lon, tempUnit)

                currentWeatherResult.fold(
                    onSuccess = { result ->
                        _state.update {
                            it.copy(
                                currentWeather = result.current,
                                location = result.location,
                                isRefreshing = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isRefreshing = false,
                                error = error.message ?: "Unknown error occurred"
                            )
                        }
                        _effect.send(WeatherEffect.ShowError(error.message ?: "Unknown error occurred"))
                    }
                )

                forecastResult.fold(
                    onSuccess = { forecast ->
                        _state.update {
                            it.copy(
                                hourlyForecast = forecast.hourlyForecast,
                                dailyForecast = forecast.dailyForecast
                            )
                        }
                    },
                    onFailure = { error ->
                        _effect.send(WeatherEffect.ShowError("Failed to load forecast: ${error.message}"))
                    }
                )
            }
        }
    }

    private fun retry() {
        val lat = lastLatitude
        val lon = lastLongitude

        if (lat != null && lon != null) {
            loadWeather(lat, lon)
        }
    }

    private fun toggleTemperatureUnit() {
        val newUnit = if (_state.value.temperatureUnit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }

        _state.update { it.copy(temperatureUnit = newUnit) }

        // Reload weather with new unit
        val lat = lastLatitude
        val lon = lastLongitude
        if (lat != null && lon != null) {
            loadWeather(lat, lon)
        }
    }
}