package com.example.firstapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Adapters.DialogCityAdaptor;
import com.example.Adapters.HourlyWeatherAdapter;
import com.example.Adapters.HourlyWeatherDto;
import com.example.Adapters.ItemMarginDecoration;
import com.example.weather.data.CityData;
import com.example.weather.data.Current;
import com.example.weather.data.Hourly;
import com.example.weather.data.WeatherResponse;
import com.example.weather.viewmodel.WeatherViewModel;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;

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

        findViewById(R.id.addCityBtn).setOnClickListener(v -> showCitySelectorDialog());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView locationIcon = findViewById(R.id.location_icon);
        locationIcon.setOnClickListener(view -> {
            if (checkLocationPermission()) {
                checkAndEnableLocation();
            }
        });

        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        int margin = getResources().getDimensionPixelSize(R.dimen.item_margin);
        hourlyRecyclerView.addItemDecoration(new ItemMarginDecoration(margin));

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        hourlyWeatherList = new ArrayList<>();
        adapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyRecyclerView.setAdapter(adapter);

        TextView cityNameTxt = findViewById(R.id.text_city_name);
        TextView temperatureTxt = findViewById(R.id.text_temperature);
        TextView weatherConditionTxt = findViewById(R.id.text_weather_condition);
        TextView highLowTempTxt = findViewById(R.id.text_high_low);


        weatherViewModel.getWeatherData().observe(this, weatherResponse -> {
            if (weatherResponse != null && weatherResponse.getHourly() != null) {
                updateHourlyWeatherList(weatherResponse);

                Current current = weatherResponse.getCurrent();
                Hourly hourly = weatherResponse.getHourly();
                Double maximumTemp = Collections.max(hourly.getTemperature2m());
                Double minimumTemp = Collections.min(hourly.getTemperature2m());

                String city = getIntent().getStringExtra("City");
                if(Objects.equals(city, null))
                    city = "Mashhad";

                cityNameTxt.setText(city);
                temperatureTxt.setText(Double.toString(current.getTemperature2m()));
                weatherConditionTxt.setText("Mostly Clear");
                highLowTempTxt.setText(MessageFormat.format("H:{0}°   L:{1}°", maximumTemp, minimumTemp));
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

    private void showCitySelectorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.city_selector_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_MyApp_Dialog)
                .setView(dialogView)
                .create();

        int width = (int) (300 * getResources().getDisplayMetrics().density);
        int height = (int) (400 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, height);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewCities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DialogCityAdaptor adapter = new DialogCityAdaptor(CityData.getCities(), city -> {
            if(CityData.FavoriteCities.stream().noneMatch(c -> c.getName().equals(city.getName())))
                CityData.FavoriteCities.add(city);

            dialog.dismiss();
        });
        recyclerView.setAdapter(adapter);

        Button closeButton = dialogView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void checkAndEnableLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                weatherViewModel.fetchWeather(latitude, longitude);

                TextView cityNameTxt = findViewById(R.id.text_city_name);
                cityNameTxt.setText("Your Location");
            } else {
                Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndEnableLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}