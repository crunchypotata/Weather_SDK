package org.iakimova.wsdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.iakimova.wsdk.cache.WeatherCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Weather SDK.
 * <p>
 * This class orchestrates weather data retrieval strategies, delegating the 
 * specifics of network communication and data formatting to the {@link WeatherClient}.
 * It manages the cache's lifecycle and ensures that users receive fresh data.
 */
public class WeatherSDKImpl implements WeatherSDK {

    private static final Logger log = LoggerFactory.getLogger(WeatherSDKImpl.class);

    private final WeatherClient weatherProvider;
    private final Mode mode;
    private final int pollingIntervalMinutes;
    private final WeatherCache cache;
    private ScheduledExecutorService scheduler;

    /**
     * Constructs a new {@code WeatherSDKImpl} instance.
     *
     * @param weatherProvider       The provider responsible for fetching and formatting weather data.
     * @param mode                  The operation mode ({@link Mode#ON_DEMAND} or {@link Mode#POLLING}).
     * @param pollingIntervalMinutes The interval in minutes for background updates (if active).
     * @param cache                 The cache implementation used for storing weather data.
     */
    public WeatherSDKImpl(
            WeatherClient weatherProvider,
            Mode mode,
            int pollingIntervalMinutes,
            WeatherCache cache
    ) {
        this.weatherProvider = weatherProvider;
        this.mode = mode;
        this.pollingIntervalMinutes = pollingIntervalMinutes;
        this.cache = cache;

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
     * Fetches fresh data from the weather provider and stores it in the local cache.
     *
     * @param city The city name.
     * @return The updated {@link WeatherResponse}.
     * @throws WeatherSDKException if the update fails.
     */
    private WeatherResponse updateWeatherForCity(String city) throws WeatherSDKException {
        WeatherResponse response = weatherProvider.getWeather(city);
        
        // Ensure timestamp is present for internal consistency if the provider didn't set it
        if (response.getDatetime() == null) {
            response.setDatetime(System.currentTimeMillis() / 1000);
        }
        
        cache.put(city, response);
        return response;
    }

    /**
     * {@inheritDoc}
     */
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
