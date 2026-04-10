package org.iakimova.wsdk;

/**
 * Configuration for the Weather SDK.
 * Use the {@link Builder} to create a customized configuration.
 */
public final class WeatherSDKConfig {
    private final Mode mode;
    private final int pollingIntervalMinutes;
    private final long cacheTtlMillis;
    private final int cacheSize;

    private WeatherSDKConfig(Builder builder) {
        this.mode = builder.mode;
        this.pollingIntervalMinutes = builder.pollingIntervalMinutes;
        this.cacheTtlMillis = builder.cacheTtlMillis;
        this.cacheSize = builder.cacheSize;
    }

    public Mode getMode() { return mode; }
    public int getPollingIntervalMinutes() { return pollingIntervalMinutes; }
    public long getCacheTtlMillis() { return cacheTtlMillis; }
    public int getCacheSize() { return cacheSize; }

    /**
     * Creates a builder with default values:
     * ON_DEMAND mode, 10 min TTL, 10 entries cache.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Mode mode = Mode.ON_DEMAND;
        private int pollingIntervalMinutes = 10;
        private long cacheTtlMillis = 10 * 60 * 1000L;
        private int cacheSize = 10;

        public Builder withMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder withPollingInterval(int minutes) {
            if (minutes <= 0) throw new IllegalArgumentException("Interval must be positive");
            this.pollingIntervalMinutes = minutes;
            return this;
        }

        public Builder withCacheTtl(long duration, java.util.concurrent.TimeUnit unit) {
            this.cacheTtlMillis = unit.toMillis(duration);
            return this;
        }

        public Builder withCacheSize(int size) {
            if (size <= 0) throw new IllegalArgumentException("Size must be positive");
            this.cacheSize = size;
            return this;
        }

        public WeatherSDKConfig build() {
            return new WeatherSDKConfig(this);
        }
    }
}
