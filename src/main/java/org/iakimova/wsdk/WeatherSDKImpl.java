package org.iakimova.wsdk;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherSDKImpl implements WeatherSDK {

    private static final Logger log = LoggerFactory.getLogger(WeatherSDKImpl.class);

    private final WeatherApiClient apiClient;
    private final WeatherJsonMapper mapper;
    private final Mode mode;

    int pollingIntervalMinutes = 10;

    private final LinkedHashMap<String, WeatherResponse> cache = new LinkedHashMap<String, WeatherResponse>(10, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, WeatherResponse> eldest) {
            return size() > 10;
        }
    };

    public WeatherSDKImpl(String apiKey, Mode mode) {
        this(apiKey, mode, 10); // default interval = 10 min
    }

    public WeatherSDKImpl(String apiKey, Mode mode, int pollingIntervalMinutes) {
        this.apiClient = new WeatherApiClient(apiKey);
        this.mapper = new WeatherJsonMapper();
        this.mode = mode;
        this.pollingIntervalMinutes = pollingIntervalMinutes;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    @Override
    public WeatherResponse getWeather(String city) throws WeatherSDKException {

        log.debug("WeatherSDK.getWeather city={}", city);

        // check cache
        WeatherResponse cached = cache.get(city);
        boolean needsUpdate = true;

        if (cached != null) {

            long lastUpdated = cached.getDatetime() * 1000; // in ms
            long now = System.currentTimeMillis();
            if (now - lastUpdated < 10 * 60 * 1000) {
                needsUpdate = false;
            }
        }

        // common logic for both modes:
        if (needsUpdate) {
            log.debug("cache MISS or EXPIRED → fetching new data for {}", city);
            String rawJson = apiClient.requestRawWeatherJson(city);
            WeatherResponse response = mapper.map(rawJson);
            cache.put(city, response);
            return response;
        } else {
            log.debug("cache HIT → return cached data for {}", city);
            return cached;
        }
    }

    @Override
    public void delete() {
        log.info("WeatherSDK.delete() called → cache cleared and polling stopped");
        cache.clear();
        stopPolling();
    }

    private ScheduledExecutorService scheduler;

    private void startPolling() {
        log.info("WeatherSDK polling started every {} minutes", pollingIntervalMinutes);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (String city : cache.keySet()) {
                    log.debug("polling refresh city={}", city);
                    String rawJson = apiClient.requestRawWeatherJson(city);
                    WeatherResponse response = mapper.map(rawJson);
                    cache.put(city, response);
                }
            } catch (WeatherSDKException e) {
                log.error("polling error", e);
            }
        }, 0, pollingIntervalMinutes, TimeUnit.MINUTES); // every 10 min
    }

    private void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            log.info("WeatherSDK polling stopped");
        }
    }
}
