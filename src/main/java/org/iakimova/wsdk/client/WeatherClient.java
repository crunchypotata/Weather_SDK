package org.iakimova.wsdk.client;

import org.iakimova.wsdk.domain.WeatherSDKException;
import org.iakimova.wsdk.domain.WeatherResponse;

/**
 * Interface for any weather API adapter.
 * <p>
 * Implementations are responsible for fetching weather data from a provider
 * and converting it into a standard {@link WeatherResponse} object.
 */
public interface WeatherClient {
    /**
     * Fetches weather information for a given city and returns a standardized response.
     *
     * @param city The name of the city.
     * @return A {@link WeatherResponse} containing the current weather.
     * @throws WeatherSDKException if the request or conversion fails.
     */
    WeatherResponse getWeather(String city) throws WeatherSDKException;
}
