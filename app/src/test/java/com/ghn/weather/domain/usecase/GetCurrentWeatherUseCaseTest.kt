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

class GetCurrentWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCurrentWeatherUseCase(repository)
    }

    @Test
    fun `invoke returns current weather and location on success`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        val result = useCase(25.7617, -80.1918, "celsius")

        result.isSuccess shouldBe true
        result.getOrNull()?.current shouldBe testCurrentWeather
        result.getOrNull()?.location shouldBe testLocation
    }

    @Test
    fun `invoke passes correct parameters to repository`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        useCase(25.7617, -80.1918, "fahrenheit")

        coVerify { repository.getWeather(25.7617, -80.1918, "fahrenheit") }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.failure(exception)

        val result = useCase(25.7617, -80.1918, "celsius")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Network error"
    }

    @Test
    fun `invoke uses celsius as default temperature unit`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns Result.success(testWeather)

        useCase(25.7617, -80.1918)

        coVerify { repository.getWeather(25.7617, -80.1918, "celsius") }
    }

    companion object {
        private val testLocation = Location(
            latitude = 25.7617,
            longitude = -80.1918,
            timezone = "America/New_York"
        )

        private val testCurrentWeather = CurrentWeather(
            time = "2024-01-15T14:00",
            temperature = 28.0,
            apparentTemperature = 30.0,
            humidity = 65,
            windSpeed = 12.5,
            weatherCode = WeatherCode.CLEAR_SKY
        )

        private val testWeather = Weather(
            location = testLocation,
            current = testCurrentWeather,
            hourlyForecast = listOf(
                HourlyWeather("2024-01-15T15:00", 29.0, 62, 11.0, WeatherCode.CLEAR_SKY)
            ),
            dailyForecast = listOf(
                DailyWeather("2024-01-15", 30.0, 22.0, WeatherCode.CLEAR_SKY, "2024-01-15T06:45", "2024-01-15T19:32")
            )
        )
    }
}
