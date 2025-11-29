package com.ghn.weather.domain.usecase

import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.Location
import com.ghn.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius"
    ): Result<CurrentWeatherResult> {
        return weatherRepository.getWeather(latitude, longitude, temperatureUnit)
            .map { weather ->
                CurrentWeatherResult(
                    current = weather.current,
                    location = weather.location
                )
            }
    }
}

data class CurrentWeatherResult(
    val current: CurrentWeather,
    val location: Location
)