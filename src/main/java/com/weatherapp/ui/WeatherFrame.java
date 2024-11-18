package com.weatherapp.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.weatherapp.model.WeatherData;
import com.weatherapp.services.GeminiService;
import com.weatherapp.services.OpenWeatherMapService;
import com.weatherapp.services.WeatherService;

public class WeatherFrame extends JFrame {
    private final WeatherService weatherService;
    private final GeminiService geminiService;
    private final ExecutorService executorService;
    private final SimpleDateFormat timeFormat;
    
    private final SearchPanel searchPanel;
    private final CurrentWeatherPanel currentWeatherPanel;
    private final JTextArea textOutputTextArea;

    // Weather data labelss
    private final JLabel cityNameLabel;
    private final JLabel temperatureLabel;
    private final JLabel humidityLabel;
    private final JLabel windSpeedLabel;
    private final JLabel pressureLabel;
    private final JLabel visibilityLabel;
    private final JLabel sunriseLabel;
    private final JLabel sunsetLabel;
    private final JLabel conditionLabel;
    private final JPanel weatherDetailsPanel;

    public WeatherFrame() {
        this.weatherService = new OpenWeatherMapService();
        this.geminiService = new GeminiService();
        this.executorService = Executors.newSingleThreadExecutor();
        this.timeFormat = new SimpleDateFormat("hh:mm a");
       
        // Initialize UI
        setTitle("Weather Advisory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));

        // Create components
        this.searchPanel = new SearchPanel(this::searchLocation);
        this.currentWeatherPanel = new CurrentWeatherPanel();
        this.textOutputTextArea = new JTextArea("Processed Data Output");
        textOutputTextArea.setLineWrap(true);
        textOutputTextArea.setWrapStyleWord(true);
        textOutputTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textOutputTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textOutputTextArea.setEditable(false);
        
        // Initialize weather data labels with styling
        this.cityNameLabel = createStyledLabel("City: ");
        this.temperatureLabel = createStyledLabel("Temperature: ");
        this.humidityLabel = createStyledLabel("Humidity: ");
        this.windSpeedLabel = createStyledLabel("Wind Speed: ");
        this.pressureLabel = createStyledLabel("Pressure: ");
        this.visibilityLabel = createStyledLabel("Visibility: ");
        this.sunriseLabel = createStyledLabel("Sunrise: ");
        this.sunsetLabel = createStyledLabel("Sunset: ");
        this.conditionLabel = createStyledLabel("Condition: ");
        
        // Initialize the weather details panel
        this.weatherDetailsPanel = createWeatherDetailsPanel();
        
        // Use BackgroundPanel as the main container
        BackgroundPanel backgroundPanel = new BackgroundPanel("weather-advisory-system\\src\\main\\resources\\images\\background.jpg");
        setContentPane(backgroundPanel);

        // Layout setup
        setupLayout(backgroundPanel);

        // Center on screen
        setLocationRelativeTo(null);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        
        // Set font to be larger and bold
        label.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Add padding around the text
        label.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        // Set colors
        label.setForeground(new Color(33, 33, 33));
        
        return label;
    }

    private JPanel createWeatherDetailsPanel() {
        JPanel panel = new JPanel();
        
        // Use GridLayout for even spacing
        panel.setLayout(new GridLayout(9, 1, 8, 8));
        
        // Add semi-transparent white background
        panel.setBackground(new Color(255, 255, 255, 180));
        
        // Add padding and border
        panel.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Add the labels
        
        
        return panel;
    }

    private void setupLayout(BackgroundPanel backgroundPanel) {
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.add(searchPanel, BorderLayout.NORTH);

        // Create main content panel with padding
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        centerPanel.add(currentWeatherPanel);
        //centerPanel.add(forecastPanel);
        
        // Create panel for details and explanation with proper spacing
        JPanel detailsAndExplanationPanel = new JPanel(new GridLayout(1, 2, 30, 30));
        detailsAndExplanationPanel.setOpaque(false);
        
        //detailsAndExplanationPanel.add(weatherDetailsPanel);
        detailsAndExplanationPanel.add(new JScrollPane(textOutputTextArea));

        centerPanel.add(detailsAndExplanationPanel);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void searchLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a location", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        textOutputTextArea.setText("Fetching weather information...");

        // Get current weather
        weatherService.getCurrentWeather(location)
            .thenAccept(weather -> {
                SwingUtilities.invokeLater(() -> {
                    currentWeatherPanel.updateWeather(weather);
                    updateWeatherDisplay(weather);
                    fetchWeatherExplanation(weather);
                });
            })
            .exceptionally(e -> {
                SwingUtilities.invokeLater(() -> {
                    handleError("Error fetching weather", e);
                });
                return null;});
            }


    private void updateWeatherDisplay(WeatherData weather) {
        
        

        // Add hover effect to labels
        for (JLabel label : new JLabel[]{
            cityNameLabel, temperatureLabel, humidityLabel, windSpeedLabel,
            pressureLabel, visibilityLabel, sunriseLabel, sunsetLabel, conditionLabel
        }) {
            addHoverEffect(label);
        }
    }

    private void addHoverEffect(JLabel label) {
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setForeground(new Color(0, 102, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(new Color(33, 33, 33));
            }
        });
    }

    private void fetchWeatherExplanation(WeatherData weather) {
        textOutputTextArea.setText("Generating weather explanation...");
        
        String weatherDescription = formatWeatherDescription(weather);
        
        executorService.submit(() -> {
            try {
                String explanation = geminiService.getExplanation(weatherDescription);
                SwingUtilities.invokeLater(() -> {
                    textOutputTextArea.setText("<html><body style='width: 300px'>" + 
                        "Weather Explanation:<br>" + explanation.replace("\n", "<br>") + 
                        "</body></html>");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    handleError("Error getting weather explanation", e);
                });
            }
        });
    }

    private String formatWeatherDescription(WeatherData weather) {
        return String.format(
            weather.getCityName(),
            weather.getTemperature(),
            weather.getCondition(),
            weather.getHumidity(),
            weather.getWindSpeed(),
            weather.getPressure(),
            weather.getVisibility(),
            timeFormat.format(new Date(weather.getSunrise() * 1000)),
            timeFormat.format(new Date(weather.getSunset() * 1000))
        );
    }

    private void handleError(String message, Throwable e) {
        String errorMessage = e.getMessage();
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = e.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(this,
            message + ": " + errorMessage,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // Call this method when closing the application
    public void cleanup() {
        executorService.shutdown();
    }
}