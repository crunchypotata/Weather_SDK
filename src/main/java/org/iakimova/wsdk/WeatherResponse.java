package org.iakimova.wsdk;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing “current weather” response from OpenWeather API.
 * All fields correspond 1:1 to OpenWeather JSON structure.
 * Temperature is returned in Kelvin, timestamps are unix seconds.
 */
@Data
@Builder
public class WeatherResponse {
    private WeatherCondition weather;
    private TemperaturePart temperature;
    private Integer visibility;
    private WindPart wind;
    private Long datetime;
    private SysPart sys;
    private Integer timezone;
    private String name;

    @Data
    @Builder
    public static class WeatherCondition {

        /** Main weather (Clouds, Rain, etc.) */
        private String main;

        /** Detailed description (scattered clouds, light rain, etc.) */
        private String description;
    }

    /**
     * Temperature data in Kelvin.
     */
    @Data
    @Builder
    public static class TemperaturePart {
        private Double temp;
        private Double feelsLike;
    }

    /**
     * Wind data in m/s
     */
    @Data
    @Builder
    public static class WindPart {
        private Double speed;
    }

    /**
     * Solar events.
     */
    @Data
    @Builder
    public static class SysPart {
        private Long sunrise;
        private Long sunset;
    }
}

