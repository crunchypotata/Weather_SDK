package org.iakimova.wsdk;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherJsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherResponse map(String rawJson) throws WeatherSDKException {
        try {
            return objectMapper.readValue(rawJson, WeatherResponse.class);
        } catch (Exception e) {
            throw new WeatherSDKException("Failed to map JSON to WeatherResponse", e);
        }
    }
}
