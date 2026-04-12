package org.iakimova.wsdk.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.iakimova.wsdk.WeatherSDK;
import org.iakimova.wsdk.domain.WeatherSDKException;
import org.iakimova.wsdk.advisor.WeatherAdvisor;
import org.iakimova.wsdk.cache.WeatherCache;
import org.iakimova.wsdk.client.WeatherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Weather SDK.
 */
public class WeatherSDKImpl implements WeatherSDK {

    private static final Logger log = LoggerFactory.getLogger(WeatherSDKImpl.class);

    private final WeatherClient weatherProvider;
    private final Mode mode;
    private final int pollingIntervalMinutes;
    private final WeatherCache cache;
    private final WeatherAdvisor advisor; // Optional AI advisor
    private ScheduledExecutorService scheduler;

    /**
     * Constructs a new {@code WeatherSDKImpl} instance.
     *
     * @param weatherProvider       The provider responsible for fetching weather data.
     * @param mode                  The operation mode.
     * @param pollingIntervalMinutes The interval in minutes for background updates.
     * @param cache                 The cache implementation.
     * @param advisor               The AI weather advisor (optional).
     */
    public WeatherSDKImpl(
            WeatherClient weatherProvider,
            Mode mode,
            int pollingIntervalMinutes,
            WeatherCache cache,
            WeatherAdvisor advisor
    ) {
        this.weatherProvider = weatherProvider;
        this.mode = mode;
        this.pollingIntervalMinutes = pollingIntervalMinutes;
        this.cache = cache;
        this.advisor = advisor;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns cached data if it's within the TTL; otherwise, fetches fresh data
     * from the provider and updates the cache.
     *
     * @param city The name of the city.
     * @return A {@link WeatherResponse} for the given city.
     * @throws WeatherSDKException if the update fails.
     */
    @Override
    public WeatherResponse getWeather(String city) throws WeatherSDKException {
        log.debug("Fetching weather for city: {}", city);

        // Cache implementation handles TTL internally. If data is stale, it returns null.
        WeatherResponse response = cache.get(city);

        if (response == null) {
            log.debug("Cache miss or expired for {}. Fetching from provider.", city);
            response = updateWeatherForCity(city);
        } else {
            log.debug("Cache hit for {}.", city);
        }

        return response;
    }

    /**
     * {@inheritDoc}
     * <p>
     * First ensures that fresh weather data is available, then asks the AI advisor 
     * for its suggestions based on that data.
     *
     * @param city The city name.
     * @return AI weather advice or a fallback message if no advisor is configured.
     * @throws WeatherSDKException if weather retrieval fails.
     */
    @Override
    public String getAIAdvice(String city) throws WeatherSDKException {
        if (advisor == null) {
            return "AI Advice is not configured. Please provide an AI API key in WeatherSDKConfig.";
        }

        log.debug("Generating AI advice for city: {}", city);
        
        // Always ensure we have fresh weather data first
        WeatherResponse weather = getWeather(city);
        
        return advisor.getAdvice(weather);
    }

    private WeatherResponse updateWeatherForCity(String city) throws WeatherSDKException {
        WeatherResponse response = weatherProvider.getWeather(city);
        
        // Ensure timestamp is present for internal consistency if the provider didn't set it
        if (response.getDatetime() == null) {
            response.setDatetime(System.currentTimeMillis() / 1000);
        }
        
        cache.put(city, response);
        return response;
    }

    @Override
    public void delete() {
        log.info("Deleting SDK instance: clearing cache and stopping background threads.");
        stopPolling();
        cache.clear();
    }

    /**
     * Starts the background polling thread for periodic updates.
     */
    private synchronized void startPolling() {
        if (scheduler != null) return;

        log.info("Starting background polling every {} minutes", pollingIntervalMinutes);
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "WeatherSDK-Polling-Thread");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            for (String city : cache.keySet()) {
                try {
                    log.debug("Polling update for city: {}", city);
                    updateWeatherForCity(city);
                } catch (Exception e) {
                    log.error("Background update failed for city: {}. Error: {}", city, e.getMessage());
                }
            }
        }, pollingIntervalMinutes, pollingIntervalMinutes, TimeUnit.MINUTES);
    }

    /**
     * Safely stops the polling executor service.
     */
    private synchronized void stopPolling() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}
