package com.ghn.weather.presentation.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghn.weather.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.ghn.weather.domain.model.CurrentWeather
import com.ghn.weather.domain.model.DailyWeather
import com.ghn.weather.domain.model.HourlyWeather
import com.ghn.weather.domain.model.Location
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.presentation.weather.viewmodel.TemperatureUnit
import com.ghn.weather.presentation.weather.viewmodel.WeatherEffect
import com.ghn.weather.presentation.weather.viewmodel.WeatherIntent
import com.ghn.weather.presentation.weather.viewmodel.WeatherState
import com.ghn.weather.presentation.weather.viewmodel.WeatherViewModel
import com.ghn.weather.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Get theme based on current weather
    val theme = state.currentWeather?.let {
        getWeatherTheme(it.weatherCode)
    } ?: getWeatherTheme(WeatherCode.UNKNOWN)

    LaunchedEffect(Unit) {
        // Default location (Miami)
        viewModel.handleIntent(WeatherIntent.LoadWeather(25.7617, -80.1918))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WeatherEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WeatherEffect.ShowLocationPermissionRequest -> {
                    // Handle location permission request
                }
            }
        }
    }

    WeatherScreenContent(
        theme = theme,
        state = state,
        snackbarHostState = snackbarHostState,
        onHandleIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreenContent(
    theme: AtmosphericTheme,
    state: WeatherState,
    snackbarHostState: SnackbarHostState,
    onHandleIntent: (WeatherIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.backgroundGradient)
    ) {
        // Ambient background animation
        AmbientBackground(theme = theme)

        Scaffold(
            topBar = {
                WeatherTopBar(
                    temperatureUnit = state.temperatureUnit,
                    onToggleUnit = { onHandleIntent(WeatherIntent.ToggleTemperatureUnit) },
                    onRefresh = { onHandleIntent(WeatherIntent.RefreshWeather) },
                    theme = theme
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onHandleIntent(WeatherIntent.RefreshWeather) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        LoadingContent(theme = theme)
                    }

                    state.error != null && state.currentWeather == null -> {
                        ErrorContent(
                            error = state.error,
                            onRetry = { onHandleIntent(WeatherIntent.Retry) },
                            theme = theme
                        )
                    }

                    state.currentWeather != null -> {
                        WeatherContent(state = state, theme = theme)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherTopBar(
    temperatureUnit: TemperatureUnit,
    onToggleUnit: () -> Unit,
    onRefresh: () -> Unit,
    theme: AtmosphericTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.weather_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemperatureUnitToggle(
                currentUnit = temperatureUnit,
                onToggle = onToggleUnit
            )

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh),
                    tint = theme.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(theme: AtmosphericTheme) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "loadingScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "loadingAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated weather icon
            WeatherIcon(
                weatherCode = WeatherCode.CLEAR_SKY,
                size = 80.dp,
                animated = true,
                tint = theme.primaryColor
            )

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Aurora,
                strokeWidth = 2.dp
            )

            Text(
                text = stringResource(R.string.loading_forecast),
                style = MaterialTheme.typography.bodyLarge,
                color = theme.textSecondary
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    theme: AtmosphericTheme
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "errorScale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .padding(32.dp)
                .scale(scale),
            theme = theme,
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Error icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TempHot.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = TempHot
                    )
                }

                Text(
                    text = stringResource(R.string.unable_to_load),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textPrimary
                )

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.textSecondary,
                    textAlign = TextAlign.Center
                )

                // Retry button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Aurora, AuroraLight)
                            )
                        )
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.try_again),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Obsidian,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(
    state: WeatherState,
    theme: AtmosphericTheme
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Current weather hero card
        state.currentWeather?.let { current ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(600)
                    )
                ) {
                    CurrentWeatherCard(
                        currentWeather = current,
                        location = state.location,
                        temperatureUnit = state.temperatureUnit,
                        sunriseTime = state.dailyForecast.firstOrNull()?.sunrise,
                        sunsetTime = state.dailyForecast.firstOrNull()?.sunset
                    )
                }
            }
        }

        // Hourly forecast section
        if (state.hourlyForecast.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(400, delayMillis = 200)
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                            title = stringResource(R.string.hourly_forecast),
                            accentColor = Aurora
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(state.hourlyForecast.take(24)) { hourly ->
                                HourlyForecastItem(
                                    hourlyWeather = hourly,
                                    temperatureUnit = state.temperatureUnit
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily forecast section
        if (state.dailyForecast.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(400, delayMillis = 400)
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                            title = stringResource(R.string.seven_day_forecast),
                            accentColor = Celestial
                        )
                    }
                }
            }

            itemsIndexed(state.dailyForecast) { index, daily ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300, delayMillis = 450 + (index * 50))) +
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = tween(300, delayMillis = 450 + (index * 50))
                            )
                ) {
                    DailyForecastItem(
                        dailyWeather = daily,
                        temperatureUnit = state.temperatureUnit
                    )
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WeatherScreenContentPreview() {
    val previewState = WeatherState(
        isLoading = false,
        isRefreshing = false,
        currentWeather = CurrentWeather(
            time = "2024-01-15T14:00",
            temperature = 28.0,
            apparentTemperature = 30.0,
            humidity = 65,
            windSpeed = 12.5,
            weatherCode = WeatherCode.CLEAR_SKY
        ),
        hourlyForecast = listOf(
            HourlyWeather("2024-01-15T15:00", 29.0, 62, 11.0, WeatherCode.CLEAR_SKY),
            HourlyWeather("2024-01-15T16:00", 28.0, 64, 10.5, WeatherCode.MAINLY_CLEAR),
            HourlyWeather("2024-01-15T17:00", 27.0, 66, 9.0, WeatherCode.PARTLY_CLOUDY),
            HourlyWeather("2024-01-15T18:00", 25.0, 70, 8.0, WeatherCode.PARTLY_CLOUDY),
            HourlyWeather("2024-01-15T19:00", 24.0, 72, 7.5, WeatherCode.CLEAR_SKY)
        ),
        dailyForecast = listOf(
            DailyWeather("2024-01-15", 30.0, 22.0, WeatherCode.CLEAR_SKY, "2024-01-15T06:45", "2024-01-15T19:32"),
            DailyWeather("2024-01-16", 28.0, 21.0, WeatherCode.PARTLY_CLOUDY, "2024-01-16T06:44", "2024-01-16T19:33"),
            DailyWeather("2024-01-17", 26.0, 20.0, WeatherCode.LIGHT_RAIN, "2024-01-17T06:44", "2024-01-17T19:34"),
            DailyWeather("2024-01-18", 25.0, 19.0, WeatherCode.MODERATE_RAIN, "2024-01-18T06:43", "2024-01-18T19:35"),
            DailyWeather("2024-01-19", 27.0, 20.0, WeatherCode.MAINLY_CLEAR, "2024-01-19T06:43", "2024-01-19T19:36"),
            DailyWeather("2024-01-20", 29.0, 21.0, WeatherCode.CLEAR_SKY, "2024-01-20T06:42", "2024-01-20T19:37"),
            DailyWeather("2024-01-21", 30.0, 22.0, WeatherCode.CLEAR_SKY, "2024-01-21T06:41", "2024-01-21T19:38")
        ),
        location = Location(
            latitude = 25.7617,
            longitude = -80.1918,
            timezone = "America/New_York"
        ),
        error = null,
        temperatureUnit = TemperatureUnit.CELSIUS
    )

    val theme = getWeatherTheme(WeatherCode.CLEAR_SKY)

    WeatherScreenContent(
        theme = theme,
        state = previewState,
        snackbarHostState = remember { SnackbarHostState() },
        onHandleIntent = {}
    )
}
