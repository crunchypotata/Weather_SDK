package org.iakimova.wsdk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherApiClient {

    private final String apiKey;
    private final OkHttpClient client;

    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    public String requestRawWeatherJson(String city) throws WeatherSDKException {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherSDKException("Failed request: " + response.code());
            }
            return response.body().string();
        } catch (Exception e) {
            throw new WeatherSDKException("HTTP request failed", e);
        }
    }
}
