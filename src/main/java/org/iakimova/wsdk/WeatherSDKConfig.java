package org.iakimova.wsdk;

import org.iakimova.wsdk.domain.Mode;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for the Weather SDK.
 */
public final class WeatherSDKConfig {
    private final Mode mode;
    private final int pollingIntervalMinutes;
    private final long cacheTtlMillis;
    private final int cacheSize;
    private final String openAiApiKey;
    private final String geminiApiKey;

    private WeatherSDKConfig(Builder builder) {
        this.mode = builder.mode;
        this.pollingIntervalMinutes = builder.pollingIntervalMinutes;
        this.cacheTtlMillis = builder.cacheTtlMillis;
        this.cacheSize = builder.cacheSize;
        this.openAiApiKey = builder.openAiApiKey;
        this.geminiApiKey = builder.geminiApiKey;
    }

    public Mode getMode() { return mode; }
    public int getPollingIntervalMinutes() { return pollingIntervalMinutes; }
    public long getCacheTtlMillis() { return cacheTtlMillis; }
    public int getCacheSize() { return cacheSize; }
    public String getOpenAiApiKey() { return openAiApiKey; }
    public String getGeminiApiKey() { return geminiApiKey; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Mode mode = Mode.ON_DEMAND;
        private int pollingIntervalMinutes = 10;
        private long cacheTtlMillis = 10 * 60 * 1000L;
        private int cacheSize = 10;
        private String openAiApiKey = null;
        private String geminiApiKey = null;

        public Builder withMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder withPollingInterval(int minutes) {
            if (minutes <= 0) throw new IllegalArgumentException("Interval must be positive");
            this.pollingIntervalMinutes = minutes;
            return this;
        }

        public Builder withCacheTtl(long duration, TimeUnit unit) {
            this.cacheTtlMillis = unit.toMillis(duration);
            return this;
        }

        public Builder withCacheSize(int size) {
            if (size <= 0) throw new IllegalArgumentException("Size must be positive");
            this.cacheSize = size;
            return this;
        }

        public Builder withOpenAiApiKey(String openAiApiKey) {
            this.openAiApiKey = openAiApiKey;
            return this;
        }

        public Builder withGeminiApiKey(String geminiApiKey) {
            this.geminiApiKey = geminiApiKey;
            return this;
        }

        public WeatherSDKConfig build() {
            return new WeatherSDKConfig(this);
        }
    }
}
