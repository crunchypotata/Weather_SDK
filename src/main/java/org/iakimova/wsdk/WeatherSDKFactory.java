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
        if (apiKey == null) {
            return; // nothing to delete
        }

        WeatherSDK sdk = instances.remove(apiKey);
        if (sdk != null) {
            // IMPORTANT: shut down background polling thread and free resources
            sdk.delete();
        }
    }
}
