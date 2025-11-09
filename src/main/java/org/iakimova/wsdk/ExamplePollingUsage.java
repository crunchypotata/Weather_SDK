package org.iakimova.wsdk;

import java.util.concurrent.TimeUnit;

public class ExamplePollingUsage {

    public static void main(String[] args) {
        String apiKey = "YOUR_OPENWEATHER_API_KEY";

        try {
            // --- создаем SDK через фабрику в режиме POLLING ---
            WeatherSDK sdk = WeatherSDKFactory.createSDK(apiKey, Mode.POLLING);

            // добавляем города в кеш
            WeatherResponse response1 = sdk.getWeather("Zocca");
            WeatherResponse response2 = sdk.getWeather("London");

            System.out.println("Initial fetch:");
            printWeather(response1);
            printWeather(response2);

            // --- имитация ожидания обновления данных ---
            System.out.println("\nWaiting for background polling to update data...\n");
            TimeUnit.SECONDS.sleep(15); // имитируем фоновые обновления

            // данные берутся почти мгновенно из кеша, но уже обновленные
            WeatherResponse updatedZocca = sdk.getWeather("Zocca");
            WeatherResponse updatedLondon = sdk.getWeather("London");

            System.out.println("After polling update:");
            printWeather(updatedZocca);
            printWeather(updatedLondon);

            // --- удаляем SDK ---
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printWeather(WeatherResponse weather) {
        System.out.println("City: " + weather.getName());
        System.out.println("Weather: " + weather.getWeather().getMain() + " - " + weather.getWeather().getDescription());
        System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K (feels like " + weather.getTemperature().getFeelsLike() + "K)");
        System.out.println("Wind speed: " + weather.getWind().getSpeed());
        System.out.println("Visibility: " + weather.getVisibility());
        System.out.println("Sunrise: " + weather.getSys().getSunrise());
        System.out.println("Sunset: " + weather.getSys().getSunset());
        System.out.println("Timezone: " + weather.getTimezone());
        System.out.println("Datetime: " + weather.getDatetime());
        System.out.println("-------------------------------");
    }
}
