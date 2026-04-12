package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;
import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherSDKException;
import org.iakimova.wsdk.domain.WeatherResponse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Example of using WeatherSDK in POLLING mode.
 * Demonstrates the use of WeatherSDKConfig and automatic background updates.
 */
public class ExamplePollingUsage {

    public static void main(String[] args) {
        // Get API key from environment variable
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Please set the OPENWEATHER_API_KEY environment variable.");
            return;
        }

        try {
            // Configure the SDK: POLLING mode, 1-minute interval, 10-minute cache TTL
            WeatherSDKConfig config = WeatherSDKConfig.builder()
                    .withMode(Mode.POLLING)
                    .withPollingInterval(1)
                    .withCacheTtl(10, TimeUnit.MINUTES)
                    .withCacheSize(10)
                    .build();

            // Get the SDK instance from the factory using the custom config
            WeatherSDK sdk = WeatherSDKFactory.getSDK(apiKey, config);

            System.out.println("Initial fetch for Barcelona and Berlin...");
            WeatherResponse response1 = sdk.getWeather("Barcelona");
            WeatherResponse response2 = sdk.getWeather("Berlin");

            printWeather(response1);
            printWeather(response2);

            // Wait for background polling to refresh the data
            System.out.println("\nWaiting for background polling (65s) to update data...\n");
            TimeUnit.SECONDS.sleep(65);

            // Fetch again - this should return updated data from the cache
            System.out.println("Fetching again (should be updated by polling):");
            WeatherResponse updatedBarcelona = sdk.getWeather("Barcelona");
            WeatherResponse updatedBerlin = sdk.getWeather("Berlin");

            printWeather(updatedBarcelona);
            printWeather(updatedBerlin);

            // Cleanup: stop polling and clear cache
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void printWeather(WeatherResponse weather) {
        if (weather == null) {
            System.out.println("Weather data not available.");
            return;
        }

        String cityName = weather.getName() != null ? weather.getName() : "Unknown";
        String description = (weather.firstWeather() != null) ? weather.firstWeather().getMain() : "N/A";
        Double temp = (weather.getTemperature() != null) ? weather.getTemperature().getTemp() : 0.0;
        String localTime = formatCityTime(weather.getDatetime(), weather.getTimezone());

        System.out.printf("[%s] Weather: %s, Temp: %.2fK, Time: %s%n",
                cityName, description, temp, localTime);
    }

    private static String formatCityTime(Long datetime, Integer timezone) {
        if (datetime == null) return "N/A";
        int offsetSeconds = (timezone != null) ? timezone : 0;
        return Instant.ofEpochSecond(datetime)
                .atZone(ZoneOffset.ofTotalSeconds(offsetSeconds))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
