package com.ghn.weather.data.repository

import com.ghn.weather.data.mapper.toDomain
import com.ghn.weather.data.remote.WeatherRemoteDataSource
import com.ghn.weather.domain.model.Weather
import com.ghn.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String
    ): Result<Weather> {
        return try {
            val response = remoteDataSource.getWeather(latitude, longitude, temperatureUnit)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}