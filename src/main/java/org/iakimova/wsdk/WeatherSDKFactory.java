package org.iakimova.wsdk;

import org.iakimova.wsdk.cache.LRUWeatherCache;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton factory for managing WeatherSDK instances.
 * <p>
 * Ensures that only one SDK instance exists per unique API Key.
 * Manages the lifecycle and shared resources of created instances.
 */
public final class WeatherSDKFactory {

    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    private WeatherSDKFactory() {}

    /**
     * Creates or retrieves an existing SDK instance for the given API key.
     * Uses default configuration and standard OpenWeather client.
     *
     * @param apiKey OpenWeather API key
     * @return Fully initialized WeatherSDK
     */
    public static WeatherSDK getSDK(String apiKey) {
        return getSDK(apiKey, WeatherSDKConfig.builder().build());
    }

    /**
     * Creates or retrieves an existing SDK instance with custom configuration.
     * Uses standard OpenWeather client.
     *
     * @param apiKey API key associated with the instance
     * @param config Custom {@link WeatherSDKConfig}
     * @return Fully initialized WeatherSDK
     */
    public static WeatherSDK getSDK(String apiKey, WeatherSDKConfig config) {
        // By default, we use the standard OpenWeather client with a shared mapper
        WeatherClient client = new WeatherApiClient(apiKey, new WeatherJsonMapper());
        return getSDK(apiKey, config, client);
    }

    /**
     * Advanced: Creates or retrieves an existing SDK instance with a custom {@link WeatherClient}.
     * This is useful for testing with stubs or providing alternative API implementations.
     *
     * @param apiKey API key to identify the instance
     * @param config Custom {@link WeatherSDKConfig}
     * @param client Custom {@link WeatherClient} implementation
     * @return Fully initialized WeatherSDK
     */
    public static WeatherSDK getSDK(String apiKey, WeatherSDKConfig config, WeatherClient client) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        return instances.computeIfAbsent(apiKey, key -> {
            LRUWeatherCache cache = new LRUWeatherCache(config.getCacheSize(), config.getCacheTtlMillis());
            
            return new WeatherSDKImpl(
                    client,
                    config.getMode(),
                    config.getPollingIntervalMinutes(),
                    cache
            );
        });
    }

    /**
     * Removes an SDK instance by API key and cleans up its resources (stops polling).
     *
     * @param apiKey API key associated with the instance to delete
     */
    public static void deleteSDK(String apiKey) {
        if (apiKey == null) return;

        WeatherSDK sdk = instances.remove(apiKey);
        if (sdk != null) {
            sdk.delete();
        }
    }
}
