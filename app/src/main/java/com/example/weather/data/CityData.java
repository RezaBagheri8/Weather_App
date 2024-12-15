package com.example.weather.data;

import java.util.ArrayList;
import java.util.List;

public class CityData {
    public static List<CityModel> getCities() {
        List<CityModel> cities = new ArrayList<>();
        cities.add(new CityModel("Tehran", 35.6892, 51.3890));
        cities.add(new CityModel("Mashhad", 36.2605, 59.6168));
        cities.add(new CityModel("Isfahan", 32.6546, 51.6680));
        cities.add(new CityModel("Shiraz", 29.5918, 52.5837));
        cities.add(new CityModel("Tabriz", 38.0800, 46.2900));
        cities.add(new CityModel("Ahvaz", 31.3183, 48.6706));
        cities.add(new CityModel("Karaj", 35.8327, 50.9915));
        cities.add(new CityModel("Qom", 34.6399, 50.8759));
        cities.add(new CityModel("Kermanshah", 34.3277, 47.0778));
        cities.add(new CityModel("Urmia", 37.5553, 45.0728));
        cities.add(new CityModel("Rasht", 37.2808, 49.5832));
        cities.add(new CityModel("Zahedan", 29.4963, 60.8629));
        cities.add(new CityModel("Hamadan", 34.7992, 48.5146));
        cities.add(new CityModel("Yazd", 31.8974, 54.3675));
        cities.add(new CityModel("Arak", 34.0917, 49.6892));
        cities.add(new CityModel("Kerman", 30.2839, 57.0834));
        cities.add(new CityModel("Sanandaj", 35.3096, 46.9994));
        cities.add(new CityModel("Bandar Abbas", 27.1865, 56.2808));
        cities.add(new CityModel("Zanjan", 36.6736, 48.4787));
        cities.add(new CityModel("Khorramabad", 33.4878, 48.3558));
        return cities;
    }
}

