package com.example.weather.data;

import java.util.List;

public class Hourly {
    private List<String> time;
    private List<Double> temperature_180m;
    private List<Double> rain;
    private List<Double> snowfall;

    public List<String> getTime() { return time; }
    public void setTime(List<String> time) { this.time = time; }
    public List<Double> getTemperature180m() { return temperature_180m; }
    public void setTemperature180m(List<Double> temperature_180m) { this.temperature_180m = temperature_180m; }
    public List<Double> getRain() { return rain; }
    public void setRain(List<Double> rain) { this.rain = rain; }
    public List<Double> getSnowfall() { return snowfall; }
    public void setSnowfall(List<Double> snowfall) { this.snowfall = snowfall; }
}
