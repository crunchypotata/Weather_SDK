package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;

public class RealWeatherExample {

    public static void main(String[] args) throws Exception {

        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("You must set env variable OPENWEATHER_API_KEY");
        }

        WeatherClient client = new WeatherApiClient(apiKey);
        WeatherSDK sdk = WeatherSDKFactory.createSDK(apiKey, client, Mode.ON_DEMAND, 10);

        WeatherResponse weather = sdk.getWeather("Barcelona");

        System.out.println("City: " + weather.getName());
        System.out.println("Temp: " + weather.getTemperature().getTemp());
        System.out.println("Weather: " + weather.firstWeather().getMain());
    }
}


