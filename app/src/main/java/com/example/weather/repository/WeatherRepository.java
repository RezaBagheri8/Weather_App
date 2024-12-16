package com.example.weather.repository;

import com.example.weather.api.WeatherApi;
import com.example.weather.data.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {
    private final WeatherApi weatherApi;

    public WeatherRepository() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        weatherApi = retrofit.create(WeatherApi.class);
    }

    public void getWeather(double latitude, double longitude, WeatherCallback callback) {
        weatherApi.getWeather(latitude, longitude, "temperature_2m,rain,snowfall",
                "temperature_2m,rain,snowfall", 1, "auto")
            .enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
    }

    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onError(String error);
    }
}