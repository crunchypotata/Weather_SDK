package org.iakimova.wsdk.cache;

import org.iakimova.wsdk.WeatherResponse;

import java.util.Set;

public interface WeatherCache {
    WeatherResponse get(String city);
    void put(String city, WeatherResponse response);
    void clear();
    Set<String> keySet();  // for iterate
}
