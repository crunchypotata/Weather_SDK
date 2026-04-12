package org.iakimova.wsdk.cache;

import java.util.LinkedHashMap;
import org.iakimova.wsdk.domain.WeatherResponse;
import java.util.Map;
import java.util.Set;

/**
 * A concrete implementation of {@link WeatherCache} using a Least Recently Used (LRU) policy
 * and a Time-To-Live (TTL) expiration strategy.
 * This adapter encapsulates the storage logic and handles internal cache state management.
 * <p>
 * This class is thread-safe using synchronized methods.
 */
public class LRUWeatherCache implements WeatherCache {

    private final long ttlMillis;
    private final Map<String, CacheEntry> cache;

    /**
     * Constructs a new {@code LRUWeatherCache} with the given maximum size and TTL.
     *
     * @param maxSize   The maximum number of entries to store in the cache.
     * @param ttlMillis The Time-To-Live for cache entries in milliseconds.
     */
    public LRUWeatherCache(int maxSize, long ttlMillis) {
        this.ttlMillis = ttlMillis;
        this.cache = new LinkedHashMap<String, CacheEntry>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > maxSize;
            }
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns null if the entry has expired or does not exist.
     *
     * @param city The name of the city to look up.
     * @return The {@link WeatherResponse} or null if the entry is not available or stale.
     */
    @Override
    public synchronized WeatherResponse get(String city) {
        CacheEntry entry = cache.get(city);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired(ttlMillis)) {
            cache.remove(city);
            return null;
        }

        return entry.getResponse();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Wraps the response in a {@link CacheEntry} with the current system time.
     *
     * @param city     The name of the city.
     * @param response The weather data to store.
     */
    @Override
    public synchronized void put(String city, WeatherResponse response) {
        cache.put(city, new CacheEntry(response));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clear() {
        cache.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns an unmodifiable copy of the city names currently in the cache
     * to prevent ConcurrentModificationException when used in other parts of the system.
     *
     * @return A thread-safe {@link Set} of city names in the cache.
     */
    @Override
    public synchronized Set<String> keySet() {
        return Set.copyOf(cache.keySet());
    }
}
