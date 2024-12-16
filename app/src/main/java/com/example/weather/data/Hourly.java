package com.example.weather.data;

import java.util.List;

public class Hourly {
    private List<String> time;
    private List<Double> temperature_2m;
    private List<Double> rain;
    private List<Double> snowfall;

    public List<String> getTime() { return time; }
    public void setTime(List<String> time) { this.time = time; }
    public List<Double> getTemperature2m() { return temperature_2m; }
    public void setTemperature2m(List<Double> temperature_2m) { this.temperature_2m = temperature_2m; }
    public List<Double> getRain() { return rain; }
    public void setRain(List<Double> rain) { this.rain = rain; }
    public List<Double> getSnowfall() { return snowfall; }
    public void setSnowfall(List<Double> snowfall) { this.snowfall = snowfall; }
}
