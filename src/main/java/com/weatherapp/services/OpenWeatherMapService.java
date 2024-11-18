package com.weatherapp.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;

public class OpenWeatherMapService implements WeatherService {
    private static final Properties config = new Properties();
    private static final String API_KEY;
    private static final String BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    static {
        try {
            // First try to load from classpath
            InputStream input = OpenWeatherMapService.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            
            // If not found in classpath, try to load from current directory
            if (input == null) {
                input = OpenWeatherMapService.class.getClassLoader()
                        .getResourceAsStream("/config.properties");
            }
            
            // If still not found, use default values
            if (input == null) {
                throw new IOException("Could not find config.properties");
            }
            
            config.load(input);
            API_KEY = config.getProperty("API_KEY");
            if (API_KEY == null || API_KEY.trim().isEmpty()) {
                throw new RuntimeException("API_KEY not found in config.properties");
            }
            
            BASE_URL = config.getProperty("BASE_URL", "https://api.openweathermap.org/data/2.5");
            
        } catch (IOException e) {
            throw new RuntimeException("Please create a config.properties file in your resources folder with API_KEY and BASE_URL properties. Error: " + e.getMessage(), e);
        }
    }

    public OpenWeatherMapService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public CompletableFuture<WeatherData> getCurrentWeather(String city) {
        String url = String.format("%s?q=%s&appid=%s", 
            BASE_URL, city, API_KEY);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Failed to fetch weather data: " + response.body());
                }
                try {
                    return parseWeatherData(objectMapper.readTree(response.body()));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to parse weather data", e);
                }
            });
    }

    @Override
    public CompletableFuture<List<ForecastData>> getForecast(String city) {
        String url = String.format("%s/forecast?q=%s&appid=%s&units=metric&cnt=7", 
            BASE_URL, city, API_KEY);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Failed to fetch forecast data: " + response.body());
                }
                try {
                    return parseForecastData(objectMapper.readTree(response.body()));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to parse forecast data", e);
                }
            });
    }

    private WeatherData parseWeatherData(JsonNode json) {
        JsonNode mainData = json.get("main");
        JsonNode weatherData = json.get("weather").get(0);
        JsonNode windData = json.get("wind");
        JsonNode sysData = json.get("sys");
    
        double temperature = mainData.get("temp").asDouble();
        double humidity = mainData.get("humidity").asDouble();
        double windSpeed = windData.get("speed").asDouble();
        double pressure = mainData.has("pressure") ? mainData.get("pressure").asDouble() : 0.0;
        int visibility = json.has("visibility") ? json.get("visibility").asInt() : 0;
        String condition = weatherData.get("description").asText();
        String iconCode = weatherData.get("icon").asText();
        String cityName = json.get("name").asText();
        
        // Optional fields: sunrise and sunset
        long sunrise = sysData.has("sunrise") ? sysData.get("sunrise").asLong() : 0;
        long sunset = sysData.has("sunset") ? sysData.get("sunset").asLong() : 0;
    
        return new WeatherData(temperature, humidity, windSpeed, condition, iconCode,
                               cityName, pressure, visibility, sunrise, sunset);
    }
    
    private List<ForecastData> parseForecastData(JsonNode json) {
        List<ForecastData> forecasts = new ArrayList<>();
        JsonNode list = json.get("list");

        for (JsonNode dayData : list) {
            JsonNode main = dayData.get("main");
            JsonNode weather = dayData.get("weather").get(0);
            
            Date date = new Date(dayData.get("dt").asLong() * 1000);
            double maxTemp = main.get("temp_max").asDouble();
            double minTemp = main.get("temp_min").asDouble();
            String weatherCondition = weather.get("description").asText();
            String iconCode = weather.get("icon").asText();
            
            forecasts.add(new ForecastData(date, maxTemp, minTemp, weatherCondition, iconCode));
        }

        return forecasts;
    }

    
}