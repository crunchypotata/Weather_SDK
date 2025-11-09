package org.iakimova.wsdk;

/**
 * Public interface for accessing current weather data using OpenWeather API.
 * <p>
 * SDK hides all networking, JSON parsing and caching logic behind this interface.
 * Caller only needs to provide city name and read WeatherResponse object.
 */
public interface WeatherSDK {

    /**
     * Returns current weather information for a given city.
     * <p>
     * IMPORTANT:
     * <ul>
     *   <li>If data for this city is already cached and is < 10 minutes old – cached value will be returned (zero network latency)</li>
     *   <li>If data is stale (>=10 minutes) – SDK will automatically request updated data</li>
     *   <li>In POLLING mode SDK updates data periodically even without calls to this method</li>
     * </ul>
     *
     * @param city city name to look up (first match is returned by OpenWeather)
     * @return WeatherResponse DTO with mapped OpenWeather fields
     * @throws WeatherSDKException if request fails, API key invalid or city not found
     */
    WeatherResponse getWeather(String city) throws WeatherSDKException;

    /**
     * Deletes SDK instance and frees resources.
     * <p>
     * After calling this method instance cannot be used anymore.
     */
    void delete();
}
