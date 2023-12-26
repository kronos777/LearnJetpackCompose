package com.example.firstjetpackcompose.data

data class WeatherResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)