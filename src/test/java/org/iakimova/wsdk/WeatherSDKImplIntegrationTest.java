package org.iakimova.wsdk;

import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WeatherSDKImpl.
 */
class WeatherSDKImplIntegrationTest {

    private WeatherSDKImpl sdk;
    private final String CITY = "Zocca";
    private static final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(10);

    // Supplier creates a FRESH object on every call to simulate real API behavior
    private final Supplier<WeatherResponse> responseSupplier = () -> 
        WeatherResponse.builder()
                .name(CITY)
                .weather(Collections.singletonList(
                        new WeatherResponse.WeatherCondition("Clouds", "scattered clouds")
                ))
                .temperature(WeatherResponse.MainData.builder()
                        .temp(269.6)
                        .feelsLike(267.57)
                        .build())
                .datetime(1675744800L)
                .timezone(3600)
                .build();

    @BeforeEach
    void setUp() {
        WeatherClient stubClient = new WeatherClientStub(responseSupplier);
        sdk = new WeatherSDKImpl(stubClient, Mode.ON_DEMAND, 10, new LRUWeatherCache(10, DEFAULT_TTL));
    }

    @Test
    void testGetWeather_CacheMissAndHit() throws WeatherSDKException {
        // First call → cache miss
        WeatherResponse response1 = sdk.getWeather(CITY);
        assertNotNull(response1);

        // Second call → cache hit (should be EXACTLY the same instance in memory)
        WeatherResponse response2 = sdk.getWeather(CITY);
        assertSame(response1, response2, "Should return the SAME object from cache (hit)");
    }

    @Test
    void testExpirationTriggersRefresh() throws WeatherSDKException, InterruptedException {
        WeatherClient stubClient = new WeatherClientStub(responseSupplier);
        // Very short TTL of 50ms
        WeatherSDKImpl shortTtlSdk = new WeatherSDKImpl(stubClient, Mode.ON_DEMAND, 10, new LRUWeatherCache(10, 50));

        WeatherResponse response1 = shortTtlSdk.getWeather(CITY);
        
        // Wait longer than TTL
        Thread.sleep(100); 

        // Second call → data was stale → cache refreshed from supplier → NEW object created
        WeatherResponse response2 = shortTtlSdk.getWeather(CITY);
        
        assertNotSame(response1, response2, "Should fetch a FRESH object after cache expiration");
        assertEquals(response1.getName(), response2.getName(), "Data content should still be correct");
    }

    @Test
    void testDeleteClearsCache() throws WeatherSDKException {
        WeatherResponse response1 = sdk.getWeather(CITY);
        sdk.delete();

        // After delete, even if TTL hasn't passed, cache is empty → new fetch
        WeatherResponse response2 = sdk.getWeather(CITY);
        assertNotSame(response1, response2);
    }
}
