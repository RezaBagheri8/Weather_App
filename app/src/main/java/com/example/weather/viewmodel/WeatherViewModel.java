package com.example.weather.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.data.WeatherResponse;
import com.example.weather.repository.WeatherRepository;

public class WeatherViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final MutableLiveData<WeatherResponse> weatherData;
    public WeatherResponse finalResponse;
    private final MutableLiveData<String> error;

    public WeatherViewModel() {
        repository = new WeatherRepository();
        weatherData = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public LiveData<WeatherResponse> getWeatherData() {
        return weatherData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchWeather(double latitude, double longitude) {
        repository.getWeather(latitude, longitude, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse response) {

                weatherData.setValue(response);
                finalResponse = response;
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
} 