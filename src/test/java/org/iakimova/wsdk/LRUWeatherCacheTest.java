package org.iakimova.wsdk;

import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LRUWeatherCacheTest {

    private LRUWeatherCache cache;
    private static final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(10);

    @BeforeEach
    void setUp() {
        cache = new LRUWeatherCache(3, DEFAULT_TTL);
    }

    @Test
    void testPutAndGet() {
        WeatherResponse response = new WeatherResponse();
        cache.put("City1", response);

        assertSame(response, cache.get("City1"));
        assertNull(cache.get("UnknownCity"));
    }

    @Test
    void testExpiration() throws InterruptedException {
        // Small TTL for testing
        LRUWeatherCache fastCache = new LRUWeatherCache(10, 100); 
        WeatherResponse response = new WeatherResponse();
        
        fastCache.put("City", response);
        assertNotNull(fastCache.get("City"));
        
        Thread.sleep(150); // wait for expiration
        
        assertNull(fastCache.get("City"), "Entry should be expired and removed");
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

        // Access City1 → makes it "recently used"
        cache.get("City1");

        // Add 4th city → should evict City2 (the oldest unused)
        cache.put("City4", new WeatherResponse());

        assertNotNull(cache.get("City1"));
        assertNull(cache.get("City2"), "City2 should be evicted by LRU policy");
        assertNotNull(cache.get("City3"));
        assertNotNull(cache.get("City4"));
    }
}
