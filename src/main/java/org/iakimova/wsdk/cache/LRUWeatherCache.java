package org.iakimova.wsdk.cache;

import org.iakimova.wsdk.WeatherResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRUWeatherCache implements WeatherCache {

    private final LinkedHashMap<String, WeatherResponse> cache;

    public LRUWeatherCache(int maxSize) {
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, WeatherResponse> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public WeatherResponse get(String city) {
        return cache.get(city);
    }

    @Override
    public void put(String city, WeatherResponse response) {
        cache.put(city, response);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }
}

