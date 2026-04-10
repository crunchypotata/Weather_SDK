package org.iakimova.wsdk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * Adapter for the OpenWeather API.
 * <p>
 * This class handles the specific communication details with OpenWeather API
 * and converts the raw JSON response into a common {@link WeatherResponse} object.
 */
public class WeatherApiClient implements WeatherClient {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final OkHttpClient SHARED_CLIENT = new OkHttpClient();

    private final String apiKey;
    private final WeatherJsonMapper mapper;

    public WeatherApiClient(String apiKey, WeatherJsonMapper mapper) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.mapper = (mapper != null) ? mapper : new WeatherJsonMapper();
    }

    @Override
    public WeatherResponse getWeather(String city) throws WeatherSDKException {
        String url = BASE_URL + "?q=" + city + "&appid=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = SHARED_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherSDKException("API request failed with code: " + response.code());
            }

            if (response.body() == null) {
                throw new WeatherSDKException("API returned an empty response body");
            }

            String rawJson = response.body().string();
            return mapper.map(rawJson);
        } catch (IOException e) {
            throw new WeatherSDKException("Network error during API request", e);
        }
    }
}
