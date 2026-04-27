package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;
import org.iakimova.wsdk.domain.WeatherSDKException;
import org.iakimova.wsdk.domain.WeatherResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class ExampleWithAI {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String weatherKey = dotenv.get("OPENWEATHER_API_KEY");
        String geminiKey = dotenv.get("GEMINI_API_KEY");

        try {
            WeatherSDKConfig config = WeatherSDKConfig.builder()
                    .withGeminiApiKey(geminiKey) // Gemini
                    .build();

            WeatherSDK sdk = WeatherSDKFactory.getSDK(weatherKey, config);
            String city = "Barcelona";

            WeatherResponse weather = sdk.getWeather(city);
            System.out.println("=== Full Weather Data ===");
            System.out.println("City: " + weather.getName());
            System.out.println("Condition: " + weather.firstWeather().getDescription());
            System.out.println("Temp: " + weather.getTemperature().getTemp() + "K");
            System.out.println("Feels Like: " + weather.getTemperature().getFeelsLike() + "K");
            System.out.println("Humidity: " + weather.getTemperature().getHumidity() + "%");
            System.out.println("Wind: " + weather.getWind().getSpeed() + " m/s");
            System.out.println("=========================");

            System.out.println("\nAI Advice: " + sdk.getAIAdvice(city));

            WeatherSDKFactory.deleteSDK(weatherKey);
        } catch (WeatherSDKException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}