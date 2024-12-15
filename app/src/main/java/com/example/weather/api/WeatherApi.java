package com.example.weather.api;

import com.example.weather.data.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("v1/forecast")
    Call<WeatherResponse> getWeather(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("current") String current,
        @Query("hourly") String hourly,
        @Query("forecast_days") int forecastDays,
        @Query("timezone") String timezone
    );
} 