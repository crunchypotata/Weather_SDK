package org.iakimova.wsdk.cache;

import org.iakimova.wsdk.domain.WeatherResponse;

/**
 * Internal wrapper for cached weather data with a timestamp.
 * Encapsulates the metadata required for cache expiration and management.
 */
class CacheEntry {
    private final WeatherResponse response;
    private final long createdAt;

    /**
     * Constructs a new {@code CacheEntry} with the given response and sets the current system time as the creation timestamp.
     *
     * @param response The {@link WeatherResponse} to wrap.
     */
    CacheEntry(WeatherResponse response) {
        this.response = response;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Gets the wrapped {@link WeatherResponse}.
     *
     * @return The cached weather data.
     */
    WeatherResponse getResponse() {
        return response;
    }

    /**
     * Checks if the entry has expired based on the given TTL.
     *
     * @param ttlMillis The Time-To-Live for cache entries in milliseconds.
     * @return true if the entry is older than the given TTL, false otherwise.
     */
    boolean isExpired(long ttlMillis) {
        return (System.currentTimeMillis() - createdAt) > ttlMillis;
    }
}
