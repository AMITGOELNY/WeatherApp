package com.ghn.weather.domain.usecase

import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius"
    ): Result<WeatherForecastResult> {
        return weatherRepository.getWeather(latitude, longitude, temperatureUnit)
            .map { weather ->
                WeatherForecastResult(
                    hourlyForecast = weather.hourlyForecast,
                    dailyForecast = weather.dailyForecast
                )
            }
    }
}

data class WeatherForecastResult(
    val hourlyForecast: List<HourlyWeather>,
    val dailyForecast: List<DailyWeather>
)