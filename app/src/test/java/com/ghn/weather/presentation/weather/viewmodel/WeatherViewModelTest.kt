package com.ghn.weather.presentation.weather.viewmodel

import app.cash.turbine.test
import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.model.Location
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.domain.usecase.CurrentWeatherResult
import com.ghn.weather.domain.usecase.GetCurrentWeatherUseCase
import com.ghn.weather.domain.usecase.GetWeatherForecastUseCase
import com.ghn.weather.domain.usecase.WeatherForecastResult
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrentWeatherUseCase = mockk()
        getWeatherForecastUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = WeatherViewModel(
        getCurrentWeatherUseCase = getCurrentWeatherUseCase,
        getWeatherForecastUseCase = getWeatherForecastUseCase
    )

    @Test
    fun `initial state is empty`() {
        viewModel = createViewModel()

        viewModel.state.value shouldBe WeatherState()
    }

    @Test
    fun `LoadWeather intent sets loading state then updates with weather data`() = runTest {
        val currentWeatherResult = CurrentWeatherResult(
            current = testCurrentWeather,
            location = testLocation
        )
        val forecastResult = WeatherForecastResult(
            hourlyForecast = testHourlyForecast,
            dailyForecast = testDailyForecast
        )

        coEvery { getCurrentWeatherUseCase(any(), any(), any()) } returns Result.success(currentWeatherResult)
        coEvery { getWeatherForecastUseCase(any(), any(), any()) } returns Result.success(forecastResult)

        viewModel = createViewModel()

        viewModel.state.value shouldBe WeatherState()

        viewModel.handleIntent(WeatherIntent.LoadWeather(25.7617, -80.1918))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.value.isLoading shouldBe false
        viewModel.state.value.currentWeather shouldBe testCurrentWeather
        viewModel.state.value.location shouldBe testLocation
    }

    @Test
    fun `LoadWeather intent handles error`() = runTest {
        val errorMessage = "Network error"
        coEvery { getCurrentWeatherUseCase(any(), any(), any()) } returns Result.failure(Exception(errorMessage))
        coEvery { getWeatherForecastUseCase(any(), any(), any()) } returns Result.failure(Exception(errorMessage))

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.handleIntent(WeatherIntent.LoadWeather(25.7617, -80.1918))

            awaitItem().isLoading shouldBe true

            val errorState = awaitItem()
            errorState.isLoading shouldBe false
            errorState.error shouldBe errorMessage
        }
    }

    @Test
    fun `ToggleTemperatureUnit switches between Celsius and Fahrenheit`() = runTest {
        coEvery { getCurrentWeatherUseCase(any(), any(), any()) } returns Result.success(
            CurrentWeatherResult(testCurrentWeather, testLocation)
        )
        coEvery { getWeatherForecastUseCase(any(), any(), any()) } returns Result.success(
            WeatherForecastResult(testHourlyForecast, testDailyForecast)
        )

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem().temperatureUnit shouldBe TemperatureUnit.FAHRENHEIT

            viewModel.handleIntent(WeatherIntent.ToggleTemperatureUnit)

            awaitItem().temperatureUnit shouldBe TemperatureUnit.CELSIUS

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RefreshWeather sets isRefreshing state`() = runTest {
        coEvery { getCurrentWeatherUseCase(any(), any(), any()) } returns Result.success(
            CurrentWeatherResult(testCurrentWeather, testLocation)
        )
        coEvery { getWeatherForecastUseCase(any(), any(), any()) } returns Result.success(
            WeatherForecastResult(testHourlyForecast, testDailyForecast)
        )

        viewModel = createViewModel()

        // First load weather to set coordinates
        viewModel.handleIntent(WeatherIntent.LoadWeather(25.7617, -80.1918))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(WeatherIntent.RefreshWeather)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.value.isRefreshing shouldBe false
        viewModel.state.value.currentWeather shouldBe testCurrentWeather
    }

    @Test
    fun `effect emits ShowError on failure`() = runTest {
        val errorMessage = "Connection failed"
        coEvery { getCurrentWeatherUseCase(any(), any(), any()) } returns Result.failure(Exception(errorMessage))
        coEvery { getWeatherForecastUseCase(any(), any(), any()) } returns Result.failure(Exception(errorMessage))

        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.handleIntent(WeatherIntent.LoadWeather(25.7617, -80.1918))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            (effect as WeatherEffect.ShowError).message shouldBe errorMessage

            cancelAndIgnoreRemainingEvents()
        }
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

        private val testHourlyForecast = listOf(
            HourlyWeather("2024-01-15T15:00", 29.0, 62, 11.0, WeatherCode.CLEAR_SKY)
        )

        private val testDailyForecast = listOf(
            DailyWeather("2024-01-15", 30.0, 22.0, WeatherCode.CLEAR_SKY, "2024-01-15T06:45", "2024-01-15T19:32")
        )
    }
}
