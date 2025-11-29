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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ghn.weather.domain.model.WeatherCode
import com.ghn.weather.ui.theme.*
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════════════════
// WEATHER SCREEN
// Premium dark-mode weather experience with atmospheric effects
// ═══════════════════════════════════════════════════════════════════════════════

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
                    onToggleUnit = { viewModel.handleIntent(WeatherIntent.ToggleTemperatureUnit) },
                    onRefresh = { viewModel.handleIntent(WeatherIntent.RefreshWeather) },
                    theme = theme
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.handleIntent(WeatherIntent.RefreshWeather) },
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
                            error = state.error ?: "Unknown error",
                            onRetry = { viewModel.handleIntent(WeatherIntent.Retry) },
                            theme = theme
                        )
                    }
                    state.currentWeather != null -> {
                        WeatherContent(
                            state = state,
                            theme = theme
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TOP BAR
// Minimal, floating top bar with controls
// ═══════════════════════════════════════════════════════════════════════════════

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
        // App title
        Text(
            text = "Weather",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary
        )

        // Controls
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
                    contentDescription = "Refresh",
                    tint = theme.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LOADING STATE
// Elegant loading animation with pulsing icon
// ═══════════════════════════════════════════════════════════════════════════════

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
                text = "Loading forecast...",
                style = MaterialTheme.typography.bodyLarge,
                color = theme.textSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ERROR STATE
// Clean error display with retry action
// ═══════════════════════════════════════════════════════════════════════════════

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
                    text = "Unable to Load",
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
                        text = "Try Again",
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

// ═══════════════════════════════════════════════════════════════════════════════
// WEATHER CONTENT
// Main scrollable content with forecast sections
// ═══════════════════════════════════════════════════════════════════════════════

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
                        temperatureUnit = state.temperatureUnit
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
                            title = "Hourly Forecast",
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
                            title = "7-Day Forecast",
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
