package org.iakimova.wsdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating SDK instances.
 * Ensures there is only one SDK instance per API key.
 */
public final class WeatherSDKFactory {

    // API key
    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    public static WeatherSDK createSDK(String apiKey, Mode mode) throws WeatherSDKException {
        return createSDK(apiKey, mode, 10); // default 10 min polling
    }

    /**
     * Returns SDK instance for given API key.
     * If instance with this key already exists – the same object will be returned.
     */
    public static WeatherSDK createSDK(String apiKey, Mode mode, int pollingIntervalMinutes) throws WeatherSDKException {
        if (instances.containsKey(apiKey)) {
            throw new WeatherSDKException("SDK with this API key already exists");
        }

        WeatherSDK sdk = new WeatherSDKImpl(apiKey, mode, pollingIntervalMinutes);
        instances.put(apiKey, sdk);
        return sdk;
    }

    /**
     * Removes SDK instance for given API key.
     */
    public static void deleteSDK(String apiKey) {
        instances.remove(apiKey);
    }
}
