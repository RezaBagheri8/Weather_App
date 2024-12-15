package com.example.Adapters;

public class HourlyWeatherDto {
    private String hour;
    private String temperature;
    private int iconResource; // Resource ID for the weather icon

    public HourlyWeatherDto(String hour, String temperature, int iconResource) {
        this.hour = hour;
        this.temperature = temperature;
        this.iconResource = iconResource;
    }

    public String getHour() { return hour; }
    public String getTemperature() { return temperature; }
    public int getIconResource() { return iconResource; }
}
