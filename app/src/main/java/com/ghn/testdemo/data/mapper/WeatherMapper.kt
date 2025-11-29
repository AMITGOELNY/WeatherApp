package com.ghn.testdemo.data.mapper

import com.ghn.testdemo.data.dto.WeatherResponseDto
import com.ghn.testdemo.domain.model.CurrentWeather
import com.ghn.testdemo.domain.model.DailyWeather
import com.ghn.testdemo.domain.model.HourlyWeather
import com.ghn.testdemo.domain.model.Location
import com.ghn.testdemo.domain.model.Weather
import com.ghn.testdemo.domain.model.WeatherCode

fun WeatherResponseDto.toDomain(): Weather {
    return Weather(
        location = Location(
            latitude = latitude,
            longitude = longitude,
            timezone = timezone
        ),
        current = current.toDomain(),
        hourlyForecast = hourly.toDomain(),
        dailyForecast = daily.toDomain()
    )
}

private fun com.ghn.testdemo.data.dto.CurrentWeatherDto.toDomain(): CurrentWeather {
    return CurrentWeather(
        time = time,
        temperature = temperature,
        apparentTemperature = apparentTemperature,
        humidity = humidity,
        windSpeed = windSpeed,
        weatherCode = WeatherCode.fromCode(weatherCode)
    )
}

private fun com.ghn.testdemo.data.dto.HourlyWeatherDto.toDomain(): List<HourlyWeather> {
    return time.indices.map { index ->
        HourlyWeather(
            time = time[index],
            temperature = temperature[index],
            humidity = humidity[index],
            windSpeed = windSpeed[index],
            weatherCode = WeatherCode.fromCode(weatherCode[index])
        )
    }
}

private fun com.ghn.testdemo.data.dto.DailyWeatherDto.toDomain(): List<DailyWeather> {
    return time.indices.map { index ->
        DailyWeather(
            date = time[index],
            temperatureMax = temperatureMax[index],
            temperatureMin = temperatureMin[index],
            weatherCode = WeatherCode.fromCode(weatherCode[index])
        )
    }
}