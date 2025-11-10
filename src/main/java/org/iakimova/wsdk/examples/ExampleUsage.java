package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;

/**
 * Example of using WeatherSDK in ON_DEMAND mode.
 */
public class ExampleUsage {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("You must set env variable OPENWEATHER_API_KEY");
        }


        try {
            // Create WeatherClient with your API key
            WeatherClient client = new WeatherApiClient(apiKey);

            // Сreate SDK via factory
            WeatherSDK sdk = WeatherSDKFactory.createSDK(apiKey, client, Mode.ON_DEMAND, 10);

            // Fetch weather for a city
            WeatherResponse weather = sdk.getWeather("Delhi");

            System.out.println("City: " + weather.getName());
            System.out.println("Weather: " + weather.firstWeather().getMain());
            System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K (feels like " + weather.getTemperature().getFeelsLike() + "K)");
            System.out.println("Wind speed: " + weather.getWind().getSpeed());
            System.out.println("Visibility: " + weather.getVisibility());
            System.out.println("Sunrise: " + weather.getSys().getSunrise());
            System.out.println("Sunset: " + weather.getSys().getSunset());
            System.out.println("Timezone: " + weather.getTimezone());
            System.out.println("Datetime: " + weather.getDatetime());

            // Delete SDK (clears cache and stops polling)
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException e) {
            System.err.println("Failed to get weather: " + e.getMessage());
        }
    }
}
