package org.iakimova.wsdk;

/**
 * Public interface for accessing current weather data.
 * <p>
 * This SDK provides a simple way to retrieve weather information for a specific city,
 * handling network communication, JSON parsing, and caching internally.
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
     * Deletes the SDK instance and releases all associated resources.
     * <p>
     * This method clears the local cache and stops any background polling threads.
     * After calling this method, the instance should no longer be used.
     */
    void delete();
}
