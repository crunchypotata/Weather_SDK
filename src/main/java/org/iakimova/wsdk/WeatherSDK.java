package org.iakimova.wsdk;

import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.iakimova.wsdk.domain.WeatherSDKException;

/**
 * Public interface for accessing current weather data and AI-based advice.
 */
public interface WeatherSDK {

    /**
     * Returns current weather information for a given city.
     * <p>
     * <b>Caching behavior:</b>
     * <ul>
     *   <li>If data for this city is already cached and is fresh (within the configured TTL) – 
     *       the cached value is returned immediately with zero network latency.</li>
     *   <li>If data is missing or stale (exceeds TTL) – the SDK will automatically 
     *       fetch updated data from the weather provider.</li>
     *   <li>In {@link Mode#POLLING} mode, the SDK updates cached data periodically
     *       in the background according to the configured interval.</li>
     * </ul>
     *
     * @param city The name of the city to look up.
     * @return A {@link WeatherResponse} object containing weather data.
     * @throws WeatherSDKException if the request fails, the API key is invalid, or the city is not found.
     */
    WeatherResponse getWeather(String city) throws WeatherSDKException;

    /**
     * Generates an AI-based weather advice for the given city.
     * <p>
     * <b>IMPORTANT:</b> This method requires an AI API key to be set in the configuration.
     * If no AI key is provided, this method may return a default message or throw an exception.
     *
     * @param city The name of the city to get advice for.
     * @return A string containing the AI's weather advice (e.g., clothing suggestions, activity ideas).
     * @throws WeatherSDKException if the advice generation fails.
     */
    String getAIAdvice(String city) throws WeatherSDKException;

    /**
     * Deletes the SDK instance and releases all associated resources.
     */
    void delete();
}
