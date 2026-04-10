package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;

/**
 * Basic example of using WeatherSDK in ON_DEMAND mode.
 * Shows how to use the simplest factory method and read basic weather data.
 */
public class ExampleUsage {

    public static void main(String[] args) {
        // Get API key from environment variable
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Please set the OPENWEATHER_API_KEY environment variable.");
            return;
        }

        try {
            // Get the SDK instance using the simplest factory method (defaults: ON_DEMAND, 10 min TTL)
            WeatherSDK sdk = WeatherSDKFactory.getSDK(apiKey);

            // Fetch weather for a city
            System.out.println("Fetching weather for Karachi...");
            WeatherResponse weather = sdk.getWeather("Karachi");

            // Print some basic info
            System.out.println("City: " + weather.getName());
            if (weather.firstWeather() != null) {
                System.out.println("Weather: " + weather.firstWeather().getMain() + " (" + weather.firstWeather().getDescription() + ")");
            }
            if (weather.getTemperature() != null) {
                System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K (feels like " + weather.getTemperature().getFeelsLike() + "K)");
            }
            System.out.println("Wind speed: " + (weather.getWind() != null ? weather.getWind().getSpeed() : "N/A"));
            System.out.println("Visibility: " + weather.getVisibility());

            // Cleanup: clears cache for this instance
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException e) {
            System.err.println("Failed to get weather: " + e.getMessage());
        }
    }
}
