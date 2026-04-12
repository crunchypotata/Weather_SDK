package org.iakimova.wsdk;

import org.iakimova.wsdk.advisor.WeatherAdvisor;
import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.iakimova.wsdk.client.WeatherClient;
import org.iakimova.wsdk.core.WeatherSDKImpl;
import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherSDKException;
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
        // Passing null as advisor for basic tests
        sdk = new WeatherSDKImpl(stubClient, Mode.ON_DEMAND, 10, new LRUWeatherCache(10, DEFAULT_TTL), null);
    }

    @Test
    void testGetWeather_CacheMissAndHit() throws WeatherSDKException {
        WeatherResponse response1 = sdk.getWeather(CITY);
        assertNotNull(response1);

        WeatherResponse response2 = sdk.getWeather(CITY);
        assertSame(response1, response2, "Should return the SAME object from cache (hit)");
    }

    @Test
    void testExpirationTriggersRefresh() throws WeatherSDKException, InterruptedException {
        WeatherClient stubClient = new WeatherClientStub(responseSupplier);
        WeatherSDKImpl shortTtlSdk = new WeatherSDKImpl(stubClient, Mode.ON_DEMAND, 10, new LRUWeatherCache(10, 50), null);

        WeatherResponse response1 = shortTtlSdk.getWeather(CITY);
        Thread.sleep(100); 

        WeatherResponse response2 = shortTtlSdk.getWeather(CITY);
        
        assertNotSame(response1, response2, "Should fetch a FRESH object after cache expiration");
        assertEquals(response1.getName(), response2.getName());
    }

    @Test
    void testGetAIAdvice_WhenNoAdvisor() throws WeatherSDKException {
        String advice = sdk.getAIAdvice(CITY);
        assertTrue(advice.contains("AI Advice is not configured"), "Should return fallback message when advisor is null");
    }

    @Test
    void testGetAIAdvice_WithStubAdvisor() throws WeatherSDKException {
        WeatherClient stubClient = new WeatherClientStub(responseSupplier);
        WeatherAdvisor stubAdvisor = weather -> "Wear a jacket";
        WeatherSDKImpl aiSdk = new WeatherSDKImpl(stubClient, Mode.ON_DEMAND, 10, new LRUWeatherCache(10, DEFAULT_TTL), stubAdvisor);

        String advice = aiSdk.getAIAdvice(CITY);
        assertEquals("Wear a jacket", advice);
    }

    @Test
    void testDeleteClearsCache() throws WeatherSDKException {
        WeatherResponse response1 = sdk.getWeather(CITY);
        sdk.delete();

        WeatherResponse response2 = sdk.getWeather(CITY);
        assertNotSame(response1, response2);
    }
}
