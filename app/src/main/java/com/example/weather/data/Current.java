package com.example.weather.data;

public class Current {
    private String time;
    private double temperature_2m;
    private double rain;
    private double snowfall;

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public double getTemperature2m() { return temperature_2m; }
    public void setTemperature2m(double temperature_2m) { this.temperature_2m = temperature_2m; }
    public Double getRain() { return rain; }
    public void setRain(Double rain) { this.rain = rain; }
    public Double getSnowfall() { return snowfall; }
    public void setSnowfall(Double snowfall) { this.snowfall = snowfall; }
}
