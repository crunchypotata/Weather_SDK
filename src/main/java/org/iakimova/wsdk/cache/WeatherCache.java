package org.iakimova.wsdk.cache;

import org.iakimova.wsdk.domain.WeatherResponse;

import java.util.Set;

/**
 * Defines the contract for a weather data cache.
 * Implementations are responsible for storing, retrieving, and managing the lifecycle of cached weather responses.
 */
public interface WeatherCache {
    /**
     * Retrieves a {@link WeatherResponse} for a given city.
     * Implementations should handle cache expiration internally and return null if the entry is stale or not found.
     *
     * @param city The name of the city.
     * @return The cached {@link WeatherResponse} if available and fresh, otherwise null.
     */
    WeatherResponse get(String city);

    /**
     * Stores a {@link WeatherResponse} for a given city.
     * Implementations should associate a timestamp with the entry to manage its expiration.
     *
     * @param city The name of the city.
     * @param response The {@link WeatherResponse} to cache.
     */
    void put(String city, WeatherResponse response);

    /**
     * Clears all entries from the cache.
     */
    void clear();

    /**
     * Returns a set of all city names currently present in the cache.
     * This is primarily used for polling mechanisms to know which cities to update.
     *
     * @return A {@link Set} of city names (keys) in the cache.
     */
    Set<String> keySet();
}
