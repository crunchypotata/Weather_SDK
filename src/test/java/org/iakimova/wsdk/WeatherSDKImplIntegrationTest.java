package org.iakimova.wsdk;

import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WeatherSDKImpl using a stub WeatherClient.
 * Adapted to new SDK: getWeather() returns a list of WeatherCondition.
 */
class WeatherSDKImplIntegrationTest {

    private WeatherSDKImpl sdk;
    private final String CITY = "Zocca";

    // Fixed JSON returned by the stub client
    private final String RAW_JSON = """
        {
          "weather": [{"main": "Clouds", "description": "scattered clouds"}],
          "temperature": {"temp": 269.6, "feels_like": 267.57},
          "visibility": 10000,
          "wind": {"speed": 1.38},
          "datetime": 1675744800,
          "sys": {"sunrise": 1675751262, "sunset": 1675787560},
          "timezone": 3600,
          "name": "Zocca"
        }
        """;

    @BeforeEach
    void setUp() {
        // Create stub client and pass to SDK
        WeatherClient stubClient = new WeatherClientStub(RAW_JSON);
        WeatherJsonMapper mapper = new WeatherJsonMapper();

        // Initialize SDK in ON_DEMAND mode with LRU cache
        sdk = new WeatherSDKImpl(stubClient, mapper, Mode.ON_DEMAND, 10, new LRUWeatherCache(10));
    }

    @Test
    void testGetWeather_CacheMissAndHit() throws WeatherSDKException {
        // First call → cache miss, fetches new data
        WeatherResponse response1 = sdk.getWeather(CITY);
        assertNotNull(response1);
        assertNotNull(response1.firstWeather());
        assertEquals("Clouds", response1.firstWeather().getMain());

        // Second call → cache hit, returns cached data
        WeatherResponse response2 = sdk.getWeather(CITY);
        assertNotNull(response2);
        assertNotNull(response2.firstWeather());
        assertEquals("Clouds", response2.firstWeather().getMain());
    }

    @Test
    void testPollingMode_RefreshesCache() throws WeatherSDKException, InterruptedException {
        // Enable POLLING mode SDK
        WeatherClient stubClient = new WeatherClientStub(RAW_JSON);
        WeatherJsonMapper mapper = new WeatherJsonMapper();
        WeatherSDKImpl pollingSdk = new WeatherSDKImpl(stubClient, mapper, Mode.POLLING, 1, new LRUWeatherCache(10));

        // Add a city to cache
        pollingSdk.getWeather(CITY);

        // Wait 2 seconds → polling should refresh the cache
        TimeUnit.SECONDS.sleep(2);

        WeatherResponse refreshed = pollingSdk.getWeather(CITY);
        assertNotNull(refreshed);
        assertNotNull(refreshed.firstWeather());
        assertEquals("Clouds", refreshed.firstWeather().getMain());

        // Stop polling to clean up
        pollingSdk.delete();
    }

    @Test
    void testDeleteClearsCache() throws WeatherSDKException {
        sdk.getWeather(CITY);
        sdk.delete();

        // After delete, cache should be empty → new fetch occurs
        WeatherResponse response = sdk.getWeather(CITY);
        assertNotNull(response);
        assertNotNull(response.firstWeather());
        assertEquals("Clouds", response.firstWeather().getMain());
    }
}
