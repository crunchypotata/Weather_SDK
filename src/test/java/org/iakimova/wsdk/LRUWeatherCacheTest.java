package org.iakimova.wsdk;

import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LRUWeatherCacheTest {

    private LRUWeatherCache cache;

    @BeforeEach
    void setUp() {
        cache = new LRUWeatherCache(3); // limit for LRU test
    }

    @Test
    void testPutAndGet() {
        WeatherResponse response = new WeatherResponse();
        cache.put("City1", response);

        assertSame(response, cache.get("City1"));
        assertNull(cache.get("UnknownCity"));
    }

    @Test
    void testClear() {
        cache.put("City1", new WeatherResponse());
        cache.put("City2", new WeatherResponse());

        cache.clear();

        assertNull(cache.get("City1"));
        assertNull(cache.get("City2"));
        assertTrue(cache.keySet().isEmpty());
    }

    @Test
    void testKeySet() {
        cache.put("City1", new WeatherResponse());
        cache.put("City2", new WeatherResponse());

        Set<String> keys = cache.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("City1"));
        assertTrue(keys.contains("City2"));
    }

    @Test
    void testLRUEviction() {
        cache.put("City1", new WeatherResponse());
        cache.put("City2", new WeatherResponse());
        cache.put("City3", new WeatherResponse());

        // Access City1 → "fresh"
        cache.get("City1");

        // Add new → should be superseded(the oldest)
        cache.put("City4", new WeatherResponse());

        assertNotNull(cache.get("City1"));
        assertNull(cache.get("City2")); // superseded
        assertNotNull(cache.get("City3"));
        assertNotNull(cache.get("City4"));
    }
}
