package com.weatherapp;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.weatherapp.ui.WeatherFrame;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
                try {
                    WeatherFrame frame = new WeatherFrame();
                    frame.setVisible(true);
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Failed to initialize weather frame", e);
                    showErrorDialog(e);
                }
            });
        } catch (javax.swing.UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.SEVERE, "Unsupported look and feel", e);
            showErrorDialog(e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Look and feel class not found", e);
            showErrorDialog(e);
        } catch (InstantiationException e) {
            LOGGER.log(Level.SEVERE, "Could not instantiate look and feel", e);
            showErrorDialog(e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Cannot access look and feel", e);
            showErrorDialog(e);
        }
    }

    private static void showErrorDialog(Exception e) {
        SwingUtilities.invokeLater(() -> {
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "An error occurred: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        });
    }
}