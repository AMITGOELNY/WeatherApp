package com.ghn.weather.data.repository

import com.ghn.weather.data.dto.CurrentWeatherDto
import com.ghn.weather.data.dto.DailyWeatherDto
import com.ghn.weather.data.dto.HourlyWeatherDto
import com.ghn.weather.data.dto.WeatherResponseDto
import com.ghn.weather.data.remote.WeatherRemoteDataSource
import com.ghn.weather.domain.model.WeatherCode
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = mockk()
        repository = WeatherRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getWeather returns mapped weather data on success`() = runTest {
        coEvery { remoteDataSource.getWeather(any(), any(), any()) } returns testWeatherResponseDto

        val result = repository.getWeather(25.7617, -80.1918, "celsius")

        result.isSuccess shouldBe true
        val weather = result.getOrNull()!!
        weather.location.latitude shouldBe 25.7617
        weather.location.longitude shouldBe -80.1918
        weather.location.timezone shouldBe "America/New_York"
        weather.current.temperature shouldBe 28.0
        weather.current.weatherCode shouldBe WeatherCode.CLEAR_SKY
    }

    @Test
    fun `getWeather passes correct parameters to data source`() = runTest {
        coEvery { remoteDataSource.getWeather(any(), any(), any()) } returns testWeatherResponseDto

        repository.getWeather(40.7128, -74.0060, "fahrenheit")

        coVerify { remoteDataSource.getWeather(40.7128, -74.0060, "fahrenheit") }
    }

    @Test
    fun `getWeather returns failure when data source throws exception`() = runTest {
        val exception = RuntimeException("API error")
        coEvery { remoteDataSource.getWeather(any(), any(), any()) } throws exception

        val result = repository.getWeather(25.7617, -80.1918, "celsius")

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "API error"
    }

    @Test
    fun `getWeather correctly maps hourly forecast`() = runTest {
        coEvery { remoteDataSource.getWeather(any(), any(), any()) } returns testWeatherResponseDto

        val result = repository.getWeather(25.7617, -80.1918, "celsius")

        val hourlyForecast = result.getOrNull()!!.hourlyForecast
        hourlyForecast.size shouldBe 2
        hourlyForecast[0].time shouldBe "2024-01-15T15:00"
        hourlyForecast[0].temperature shouldBe 29.0
        hourlyForecast[1].time shouldBe "2024-01-15T16:00"
    }

    @Test
    fun `getWeather correctly maps daily forecast`() = runTest {
        coEvery { remoteDataSource.getWeather(any(), any(), any()) } returns testWeatherResponseDto

        val result = repository.getWeather(25.7617, -80.1918, "celsius")

        val dailyForecast = result.getOrNull()!!.dailyForecast
        dailyForecast.size shouldBe 2
        dailyForecast[0].date shouldBe "2024-01-15"
        dailyForecast[0].temperatureMax shouldBe 30.0
        dailyForecast[0].temperatureMin shouldBe 22.0
    }

    companion object {
        private val testWeatherResponseDto = WeatherResponseDto(
            latitude = 25.7617,
            longitude = -80.1918,
            timezone = "America/New_York",
            current = CurrentWeatherDto(
                time = "2024-01-15T14:00",
                temperature = 28.0,
                apparentTemperature = 30.0,
                humidity = 65,
                windSpeed = 12.5,
                weatherCode = 0
            ),
            hourly = HourlyWeatherDto(
                time = listOf("2024-01-15T15:00", "2024-01-15T16:00"),
                temperature = listOf(29.0, 28.0),
                humidity = listOf(62, 64),
                windSpeed = listOf(11.0, 10.5),
                weatherCode = listOf(0, 1)
            ),
            daily = DailyWeatherDto(
                time = listOf("2024-01-15", "2024-01-16"),
                temperatureMax = listOf(30.0, 28.0),
                temperatureMin = listOf(22.0, 21.0),
                weatherCode = listOf(0, 2),
                sunrise = listOf("2024-01-15T06:45", "2024-01-16T06:44"),
                sunset = listOf("2024-01-15T19:32", "2024-01-16T19:33")
            )
        )
    }
}
