package com.weatherapp.model;

public class WeatherData {
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String condition;
    private String iconCode;
    private String cityName;
    private double pressure;
    private int visibility;
    private long sunrise;
    private long sunset;

    public WeatherData(double temperature, double humidity, double windSpeed,
                       String condition, String iconCode, String cityName,
                       double pressure, int visibility, long sunrise, long sunset) {
        this.temperature = temperature - 273.15; // Convert Kelvin to Celsius
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.iconCode = iconCode;
        this.cityName = cityName;
        this.pressure = pressure;
        this.visibility = visibility;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    // Getters
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getCondition() { return condition; }
    public String getIconCode() { return iconCode; }
    public String getCityName() { return cityName; }
    public double getPressure() { return pressure; }
    public int getVisibility() { return visibility; }
    public long getSunrise() { return sunrise; }
    public long getSunset() { return sunset; }
}
