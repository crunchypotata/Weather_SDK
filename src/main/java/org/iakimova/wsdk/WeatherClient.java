package org.iakimova.wsdk;

/**
 * Interface for any weather API client.
 * Provides raw JSON weather data for a given city.
 */
public interface WeatherClient {
    /**
     * Fetches raw weather JSON for a given city.
     *
     * @param city city name
     * @return raw JSON string
     * @throws WeatherSDKException in case of any failure
     */
    String getRawWeatherJson(String city) throws WeatherSDKException;
}
