package com.example.weather.data;

public class Current {
    private String time;
    private double temperature_180m;
    private double rain;
    private double snowfall;

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public double getTemperature180m() { return temperature_180m; }
    public void setTemperature2m(double temperature_180m) { this.temperature_180m = temperature_180m; }
    public Double getRain() { return rain; }
    public void setRain(Double rain) { this.rain = rain; }
    public Double getSnowfall() { return snowfall; }
    public void setSnowfall(Double snowfall) { this.snowfall = snowfall; }
}
