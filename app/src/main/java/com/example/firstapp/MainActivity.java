package com.example.firstapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Adapters.HourlyWeatherAdapter;
import com.example.Adapters.HourlyWeatherDto;
import com.example.weather.data.WeatherResponse;
import com.example.weather.viewmodel.WeatherViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView hourlyRecyclerView;
    private HourlyWeatherAdapter adapter;
    private List<HourlyWeatherDto> hourlyWeatherList;
    private WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FrameLayout citiesBtn = findViewById(R.id.goToCitiesButton);

        citiesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CitiesActivity.class);
            startActivity(intent);
        });

        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        hourlyWeatherList = new ArrayList<>();
        adapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyRecyclerView.setAdapter(adapter);

        weatherViewModel.getWeatherData().observe(this, weatherResponse -> {
            if (weatherResponse != null && weatherResponse.getHourly() != null) {
                updateHourlyWeatherList(weatherResponse);
            }
        });

        double latitude = getIntent().getDoubleExtra("LATITUDE", 36.2981);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 59.6057);

        weatherViewModel.fetchWeather(latitude, longitude);
    }

    private void updateHourlyWeatherList(WeatherResponse weatherResponse) {
        List<HourlyWeatherDto> updatedList = new ArrayList<>();

        List<String> times = weatherResponse.getHourly().getTime();
        List<Double> temperatures = weatherResponse.getHourly().getTemperature2m();
        List<Double> rains = weatherResponse.getHourly().getRain();
        List<Double> snowFalls = weatherResponse.getHourly().getSnowfall();

        for (int i = 0; i < times.size(); i++) {
            String hour = times.get(i);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                LocalDateTime dateTime = LocalDateTime.parse(hour, inputFormatter);

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                hour = dateTime.format(timeFormatter);
            }

            String temperature = temperatures.get(i) + "°";
            Double rain = rains.get(i);
            Double snowFall = snowFalls.get(i);

            int hourInt = Integer.parseInt(hour.substring(0, 2));
            int iconResource = getIconResource(rain, snowFall, hourInt);

            updatedList.add(new HourlyWeatherDto(hour, temperature, iconResource));
        }

        hourlyWeatherList.clear();
        hourlyWeatherList.addAll(updatedList);
        adapter.notifyDataSetChanged();
    }

    private static int getIconResource(Double rain, Double snowFall, int hourInt) {
        int iconResource;

        if(hourInt > 6 && hourInt < 17)
            iconResource = R.drawable.sunny;
        else
            iconResource = R.drawable.moony;

        if(rain > 0 && rain > snowFall)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(hourInt > 6 && hourInt < 17)
                    iconResource = R.drawable.rain_day;
                else
                    iconResource = R.drawable.rain_night;
            }
        }
        if(snowFall > 0 && snowFall > rain){
            iconResource = R.drawable.snow_day;
        }
        return iconResource;
    }
}