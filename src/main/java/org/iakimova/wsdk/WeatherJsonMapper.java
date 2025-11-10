package org.iakimova.wsdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class WeatherJsonMapper {

    private final ObjectMapper objectMapper;

    public WeatherJsonMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public WeatherResponse map(String rawJson) throws WeatherSDKException {
        try {
            return objectMapper.readValue(rawJson, WeatherResponse.class);
        } catch (Exception e) {
            throw new WeatherSDKException("Failed to map JSON to WeatherResponse", e);
        }
    }
}
