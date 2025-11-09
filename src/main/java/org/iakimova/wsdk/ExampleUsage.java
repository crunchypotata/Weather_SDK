package org.iakimova.wsdk;

public class ExampleUsage {

    public static void main(String[] args) {
        String apiKey = "YOUR_OPENWEATHER_API_KEY";

        try {
            // --- создаем SDK через фабрику ---
            WeatherSDK sdk = WeatherSDKFactory.createSDK(apiKey, Mode.ON_DEMAND);

            // --- получаем погоду для города ---
            WeatherResponse weather = sdk.getWeather("Zocca");
            System.out.println("City: " + weather.getName());
            System.out.println("Weather: " + weather.getWeather().getMain() + " - " + weather.getWeather().getDescription());
            System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K (feels like " + weather.getTemperature().getFeelsLike() + "K)");
            System.out.println("Wind speed: " + weather.getWind().getSpeed());
            System.out.println("Visibility: " + weather.getVisibility());
            System.out.println("Sunrise: " + weather.getSys().getSunrise());
            System.out.println("Sunset: " + weather.getSys().getSunset());
            System.out.println("Timezone: " + weather.getTimezone());
            System.out.println("Datetime: " + weather.getDatetime());

            // --- удаляем SDK (очищаем кеш и остановка polling) ---
            WeatherSDKFactory.deleteSDK(apiKey);

        } catch (WeatherSDKException e) {
            System.err.println("Failed to get weather: " + e.getMessage());
        }
    }
}
