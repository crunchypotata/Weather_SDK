package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Example of using WeatherSDK in POLLING mode.
 * Shows cache updates, datetime, and timezone handling.
 */
public class ExamplePollingUsage {

    public static void main(String[] args) {
        // Get API key from environment variable
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("You must set env variable OPENWEATHER_API_KEY");
        }

        try {
            // Create WeatherClient with your API key
            WeatherClient client = new WeatherApiClient(apiKey);

            // Create SDK in POLLING mode, refresh every 1 minute
            WeatherSDK sdk = WeatherSDKFactory.createSDK(apiKey, client, Mode.POLLING, 1);

            // Initial fetch — populate cache
            WeatherResponse response1 = sdk.getWeather("Barcelona");
            WeatherResponse response2 = sdk.getWeather("Berlin");

            System.out.println("Initial fetch:");
            printWeather(response1);
            printWeather(response2);

            // Wait for background polling to refresh cache
            System.out.println("\nWaiting for background polling to update data...\n");
            TimeUnit.SECONDS.sleep(70); // >1 minute to let polling update cache

            // Fetch again — should use refreshed cache
            WeatherResponse updatedBarcelona = sdk.getWeather("Barcelona");
            WeatherResponse updatedBerlin = sdk.getWeather("Berlin");

            System.out.println("After polling update:");
            printWeather(updatedBarcelona);
            printWeather(updatedBerlin);

            // Delete SDK instance (clears cache and stops polling)
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prints detailed weather information including formatted datetime.
     */
    private static void printWeather(WeatherResponse weather) {
        if (weather == null) {
            System.out.println("Weather data not available");
            return;
        }

        System.out.println("City: " + (weather.getName() != null ? weather.getName() : "N/A"));
        System.out.println("Weather: " + (weather.firstWeather() != null ? weather.firstWeather().getMain() : "N/A"));
        System.out.println("Temperature: " +
                (weather.getTemperature() != null ? weather.getTemperature().getTemp() + "K" : "N/A") +
                " (feels like " +
                (weather.getTemperature() != null ? weather.getTemperature().getFeelsLike() + "K" : "N/A") + ")");
        System.out.println("Wind speed: " + (weather.getWind() != null ? weather.getWind().getSpeed() : "N/A"));
        System.out.println("Visibility: " + (weather.getVisibility() != null ? weather.getVisibility() : "N/A"));
        System.out.println("Sunrise: " + formatCityTime(
                weather.getSys() != null ? weather.getSys().getSunrise() : null,
                weather.getTimezone()));
        System.out.println("Sunset: " + formatCityTime(
                weather.getSys() != null ? weather.getSys().getSunset() : null,
                weather.getTimezone()));
        System.out.println("Timezone offset (seconds): " + (weather.getTimezone() != null ? weather.getTimezone() : "N/A"));
        System.out.println("Datetime: " + formatCityTime(weather.getDatetime(), weather.getTimezone()));
        System.out.println("-------------------------------");
    }

    /**
     * Converts UNIX timestamp + timezone offset to formatted local time string.
     */
    private static String formatCityTime(Long datetime, Integer timezone) {
        if (datetime == null) return "N/A";
        if (timezone == null) timezone = 0;

        Instant instant = Instant.ofEpochSecond(datetime);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezone);
        ZonedDateTime zonedDateTime = instant.atZone(offset);

        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
