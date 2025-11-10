package org.iakimova.wsdk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Weather API client for OpenWeather.
 * Stores its own API key independently from SDK.
 */
public class WeatherApiClient implements WeatherClient {

    private final String apiKey;
    private final OkHttpClient client;

    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    @Override
    public String getRawWeatherJson(String city) throws WeatherSDKException {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherSDKException("Failed request: " + response.code());
            }
            if (response.body() == null) {
                throw new WeatherSDKException("Empty response body");
            }
            return response.body().string();
        } catch (Exception e) {
            throw new WeatherSDKException("HTTP request failed", e);
        }
    }
}
