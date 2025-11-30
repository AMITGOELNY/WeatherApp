package com.ghn.weather.domain.usecase

import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.model.Location
import com.ghn.weather.domain.model.Weather
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.domain.repository.WeatherRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherForecastUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherForecastUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetWeatherForecastUseCase(repository)
    }

    @Test
    fun `invoke returns hourly and daily forecast on success`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        val result = useCase(25.7617, -80.1918, "celsius")

        result.isSuccess shouldBe true
        result.getOrNull()?.hourlyForecast shouldBe testHourlyForecast
        result.getOrNull()?.dailyForecast shouldBe testDailyForecast
    }

    @Test
    fun `invoke passes correct parameters to repository`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        useCase(40.7128, -74.0060, "fahrenheit")

        coVerify { repository.getWeather(40.7128, -74.0060, "fahrenheit") }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val exception = Exception("Server error")
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.failure(exception)

        val result = useCase(25.7617, -80.1918, "celsius")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Server error"
    }

    @Test
    fun `invoke uses celsius as default temperature unit`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        useCase(25.7617, -80.1918)

        coVerify { repository.getWeather(25.7617, -80.1918, "celsius") }
    }

    companion object {
        private val testHourlyForecast = listOf(
            HourlyWeather("2024-01-15T15:00", 29.0, 62, 11.0, WeatherCode.CLEAR_SKY),
            HourlyWeather("2024-01-15T16:00", 28.0, 64, 10.5, WeatherCode.MAINLY_CLEAR)
        )

        private val testDailyForecast = listOf(
            DailyWeather("2024-01-15", 30.0, 22.0, WeatherCode.CLEAR_SKY),
            DailyWeather("2024-01-16", 28.0, 21.0, WeatherCode.PARTLY_CLOUDY)
        )

        private val testWeather = Weather(
            location = Location(25.7617, -80.1918, "America/New_York"),
            current = CurrentWeather(
                time = "2024-01-15T14:00",
                temperature = 28.0,
                apparentTemperature = 30.0,
                humidity = 65,
                windSpeed = 12.5,
                weatherCode = WeatherCode.CLEAR_SKY
            ),
            hourlyForecast = testHourlyForecast,
            dailyForecast = testDailyForecast
        )
    }
}
