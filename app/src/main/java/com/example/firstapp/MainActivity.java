package com.example.firstapp;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Adapters.HourlyWeatherAdapter;
import com.example.Adapters.HourlyWeatherDto;
import com.example.Adapters.ItemMarginDecoration;
import com.example.weather.data.Current;
import com.example.weather.data.Hourly;
import com.example.weather.data.WeatherResponse;
import com.example.weather.viewmodel.WeatherViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView hourlyRecyclerView;
    private HourlyWeatherAdapter adapter;
    private List<HourlyWeatherDto> hourlyWeatherList;
    private WeatherViewModel weatherViewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View expandedContent;

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

        View bottomSheet = findViewById(R.id.bottom_sheet);
        expandedContent = findViewById(R.id.expanded_content);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(850); // ارتفاع اولیه به dp
        bottomSheetBehavior.setHideable(false); // جلوگیری از بسته شدن کامل

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    expandedContent.setVisibility(View.VISIBLE);
                } else {
                    expandedContent.setVisibility(View.GONE);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // می‌توانید انیمیشن‌های اضافی اینجا اضافه کنید
            }
        });

        setupSkeletonLoading();

        ViewGroup cityDetailsSkeletonContainer = findViewById(R.id.city_details_skeleton_container);

        // انیمیت کردن همه فرزندان کانتینر
        for (int i = 0; i < cityDetailsSkeletonContainer.getChildCount(); i++) {
            View skeletonBox = cityDetailsSkeletonContainer.getChildAt(i);

            ObjectAnimator animator = ObjectAnimator.ofFloat(skeletonBox, "alpha", 0.5f, 1f);
            animator.setDuration(600);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            // تاخیر متفاوت برای هر باکس
            animator.setStartDelay(i * 10);
            animator.start();
        }

        FrameLayout citiesBtn = findViewById(R.id.goToCitiesButton);

        citiesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CitiesActivity.class);
            startActivity(intent);
        });

        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        int margin = getResources().getDimensionPixelSize(R.dimen.item_margin);
        hourlyRecyclerView.addItemDecoration(new ItemMarginDecoration(margin));

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        hourlyWeatherList = new ArrayList<>();
        adapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyRecyclerView.setAdapter(adapter);

        // Assuming you have the weather data
        TextView cityNameTxt = findViewById(R.id.text_city_name);
        TextView temperatureTxt = findViewById(R.id.text_temperature);
        TextView weatherConditionTxt = findViewById(R.id.text_weather_condition);
        TextView highLowTempTxt = findViewById(R.id.text_high_low);
        ViewGroup hourlySkeletonContainer = findViewById(R.id.hourly_skeleton_container);

        weatherViewModel.getWeatherData().observe(this, weatherResponse -> {
            if (weatherResponse != null && weatherResponse.getHourly() != null) {
                findViewById(R.id.hourly_skeleton_container).setVisibility(View.GONE);
                cityDetailsSkeletonContainer.setVisibility(View.GONE);
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
            } else {
                findViewById(R.id.hourly_skeleton_container).setVisibility(View.VISIBLE);
                cityDetailsSkeletonContainer.setVisibility(View.VISIBLE);
            }
        });

        double latitude = getIntent().getDoubleExtra("LATITUDE", 36.2981);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 59.6057);

        findViewById(R.id.hourly_skeleton_container).setVisibility(View.VISIBLE);
        cityDetailsSkeletonContainer.setVisibility(View.VISIBLE);
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

    private void setupSkeletonLoading() {
        LinearLayout container = findViewById(R.id.hourly_skeleton_container);

        // تعداد کامپوننت‌های مورد نیاز
        int componentCount = 24;

        for (int i = 0; i < componentCount; i++) {
            View skeletonComponent = new View(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(60),
                    dpToPx(146)
            );
            params.rightMargin = dpToPx(10);

            skeletonComponent.setLayoutParams(params);
            skeletonComponent.setBackground(getDrawable(R.drawable.hourly_skeleton_background));
            skeletonComponent.setPadding(
                    dpToPx(8),
                    dpToPx(16),
                    dpToPx(8),
                    dpToPx(15)
            );

            container.addView(skeletonComponent);

            ObjectAnimator animator = ObjectAnimator.ofFloat(skeletonComponent, "alpha", 0.5f, 1f);
            animator.setDuration(600);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.setStartDelay(i * 10);
            animator.start();
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}