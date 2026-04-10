package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;

/**
 * Real-world example of using the Weather SDK to fetch detailed weather data.
 * Demonstrates basic usage with default configuration.
 */
public class RealWeatherExample {

    public static void main(String[] args) {
        // Get API key from environment variable
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Please set the OPENWEATHER_API_KEY environment variable.");
            return;
        }

        try {
            // Get the SDK instance using the default configuration
            WeatherSDK sdk = WeatherSDKFactory.getSDK(apiKey);

            // Fetch weather for Barcelona
            System.out.println("Fetching weather for Barcelona...");
            WeatherResponse weather = sdk.getWeather("Barcelona");

            // Print detailed information
            System.out.println("--------------------------------");
            System.out.println("City: " + weather.getName());
            
            if (weather.firstWeather() != null) {
                System.out.println("Condition: " + weather.firstWeather().getMain() + " (" + weather.firstWeather().getDescription() + ")");
            }
            
            if (weather.getTemperature() != null) {
                System.out.printf("Temperature: %.2fK (Feels like: %.2fK)%n", 
                        weather.getTemperature().getTemp(), 
                        weather.getTemperature().getFeelsLike());
                System.out.println("Humidity: " + weather.getTemperature().getHumidity() + "%");
                System.out.println("Pressure: " + weather.getTemperature().getPressure() + " hPa");
            }
            
            if (weather.getWind() != null) {
                System.out.println("Wind speed: " + weather.getWind().getSpeed() + " m/s");
                System.out.println("Wind degree: " + weather.getWind().getDeg() + "°");
            }
            
            System.out.println("Visibility: " + (weather.getVisibility() != null ? weather.getVisibility() + "m" : "N/A"));
            System.out.println("--------------------------------");

            // Cleanup: remove the instance and clear resources
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException e) {
            System.err.println("SDK Error: " + e.getMessage());
        }
    }
}
