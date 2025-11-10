package org.iakimova.wsdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.iakimova.wsdk.cache.WeatherCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SDK implementation. Fully agnostic to API provider.
 * Key changes:
 * 1) API key is no longer passed to SDK — it lives in the WeatherClient.
 * 2) SDK only depends on WeatherClient interface.
 * 3) Constructors with String apiKey removed to enforce DI.
 */
public class WeatherSDKImpl implements WeatherSDK {

    private static final Logger log = LoggerFactory.getLogger(WeatherSDKImpl.class);
    private static final long TTL_MS = 10 * 60 * 1000; // 10 min

    private final WeatherClient apiClient;
    private final WeatherJsonMapper mapper;
    private final Mode mode;
    private final int pollingIntervalMinutes;
    private final WeatherCache cache;
    private ScheduledExecutorService scheduler;

    /**
     * Main constructor using dependency injection.
     * SDK is now completely agnostic to API provider.
     *
     * @param apiClient Any implementation of WeatherClient (OpenWeather, stub, etc.)
     * @param mapper    JSON mapper
     * @param mode      ON_DEMAND or POLLING
     * @param pollingIntervalMinutes interval for polling mode
     * @param cache     Cache implementation
     */
    public WeatherSDKImpl(
            WeatherClient apiClient,
            WeatherJsonMapper mapper,
            Mode mode,
            int pollingIntervalMinutes,
            WeatherCache cache
    ) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.mode = mode;
        this.pollingIntervalMinutes = pollingIntervalMinutes;
        this.cache = cache;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    /**
     * Retrieves weather for a city. Checks cache first (TTL 10min).
     * @param city City name
     * @return WeatherResponse
     * @throws WeatherSDKException
     */
    @Override
    public WeatherResponse getWeather(String city) throws WeatherSDKException {

        log.debug("WeatherSDK.getWeather city={}", city);

        WeatherResponse cached = cache.get(city);
        boolean needsUpdate = true;

        if (cached != null) {
            Long dt = cached.getDatetime();
            if (dt != null) {
                long lastUpdated = dt * 1000; // convert to ms
                long now = System.currentTimeMillis();
                if (now - lastUpdated < TTL_MS) {
                    needsUpdate = false; // cache hit
                }
            } else {
                log.debug("cached datetime is null for {}, forcing update", city);
            }
        }

        if (needsUpdate) {
            log.debug("cache MISS or EXPIRED → fetching new data for {}", city);
            String rawJson = apiClient.getRawWeatherJson(city);
            WeatherResponse response = mapper.map(rawJson);

            // На всякий случай: если API не возвращает datetime, ставим текущий момент
            if (response.getDatetime() == null) {
                response.setDatetime(System.currentTimeMillis() / 1000); // seconds
            }

            cache.put(city, response);
            return response;
        } else {
            log.debug("cache HIT → return cached data for {}", city);
            return cached;
        }
    }

    /**
     * Clears cache and stops polling.
     */
    @Override
    public void delete() {
        log.info("WeatherSDK.delete() called → cache cleared and polling stopped");
        cache.clear();
        stopPolling();
    }

    /**
     * Starts polling thread if mode == POLLING.
     */
    private void startPolling() {
        log.info("WeatherSDK polling started every {} minutes", pollingIntervalMinutes);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (String city : cache.keySet()) {
                    log.debug("polling refresh city={}", city);
                    String rawJson = apiClient.getRawWeatherJson(city);
                    WeatherResponse response = mapper.map(rawJson);
                    cache.put(city, response);
                }
            } catch (WeatherSDKException e) {
                log.error("polling error", e);
            }
        }, 0, pollingIntervalMinutes, TimeUnit.MINUTES);
    }

    /**
     * Stops polling thread.
     */
    private void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            log.info("WeatherSDK polling stopped");
        }
    }
}
