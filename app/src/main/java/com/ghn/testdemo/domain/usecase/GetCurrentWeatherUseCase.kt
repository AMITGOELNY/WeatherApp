package com.ghn.testdemo.domain.usecase

import com.ghn.testdemo.domain.model.CurrentWeather
import com.ghn.testdemo.domain.model.Location
import com.ghn.testdemo.domain.repository.WeatherRepository
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