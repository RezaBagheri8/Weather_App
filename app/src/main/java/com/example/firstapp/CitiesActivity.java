package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Adapters.CityAdapter;
import com.example.weather.data.CityData;
import com.example.weather.data.CityModel;

import java.util.List;

public class CitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cities);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CityModel> cities = CityData.getCities();
        CityAdapter adapter = new CityAdapter(cities, city -> {
            double latitude = city.getLatitude();
            double longitude = city.getLongitude();

            Toast.makeText(this, "Selected: " + city.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("City", city.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}