package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;
import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.iakimova.wsdk.domain.WeatherSDKException;

import java.util.concurrent.TimeUnit;

/**
 * Basic example of using WeatherSDK in ON_DEMAND mode with custom configuration.
 * Demonstrates the use of the simplest factory method and manual cache adjustment.
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
            // Option 1: Quick start using default configuration (ON_DEMAND, 10 min TTL)
            WeatherSDK sdkDefault = WeatherSDKFactory.getSDK(apiKey);
            System.out.println("Quick fetch for Karachi: " + sdkDefault.getWeather("Karachi").getName());

            // Option 2: Custom configuration for high-traffic scenarios
            WeatherSDKConfig customConfig = WeatherSDKConfig.builder()
                    .withMode(Mode.ON_DEMAND)
                    .withCacheTtl(1, TimeUnit.HOURS)
                    .withCacheSize(500)
                    .build();

            // Note: In reality, getSDK will return the existing instance for the same key.
            // If you need to change config, delete the old instance first.
            WeatherSDKFactory.deleteSDK(apiKey);
            WeatherSDK sdkCustom = WeatherSDKFactory.getSDK(apiKey, customConfig);

            // Fetch weather for Barcelona with extended data
            WeatherResponse weather = sdkCustom.getWeather("Barcelona");
            System.out.println("\n--- Detailed Weather for " + weather.getName() + " ---");
            System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K (Feels like: " + weather.getTemperature().getFeelsLike() + "K)");
            System.out.println("Humidity: " + weather.getTemperature().getHumidity() + "%");
            System.out.println("Condition: " + weather.firstWeather().getDescription());
            System.out.println("Wind Speed: " + weather.getWind().getSpeed() + " m/s");
            System.out.println("Visibility: " + weather.getVisibility() + "m");
            System.out.println("----------------------------------------");

            // Cleanup: stops background threads and clears cache for this API key
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException e) {
            System.err.println("Failed to get weather: " + e.getMessage());
        }
    }
}
