package org.iakimova.wsdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory to create SDK instances per API key.
 * Prevents creating two SDK instances with the same key.
 */
public final class WeatherSDKFactory {

    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    public static WeatherSDK createSDK(String apiKey, WeatherClient client, Mode mode, int pollingIntervalMinutes) throws WeatherSDKException {
        return instances.computeIfAbsent(apiKey, key ->
                new WeatherSDKImpl(client, new WeatherJsonMapper(), mode, pollingIntervalMinutes, new org.iakimova.wsdk.cache.LRUWeatherCache(10))
        );
    }

    public static void deleteSDK(String apiKey) {
        instances.remove(apiKey);
    }
}
