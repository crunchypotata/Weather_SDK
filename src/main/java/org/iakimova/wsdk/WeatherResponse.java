package org.iakimova.wsdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO representing “current weather” response from OpenWeather API.
 * All fields correspond 1:1 to OpenWeather JSON structure.
 * Temperature is returned in Kelvin, timestamps are unix seconds.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private List<WeatherCondition> weather;
    @JsonProperty("main")
    private TemperaturePart temperature;
    private Integer visibility;
    private WindPart wind;
    private Long datetime;
    private SysPart sys;
    private Integer timezone;
    private String name;
    public WeatherCondition firstWeather() {
        return (weather == null || weather.isEmpty()) ? null : weather.get(0);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TemperaturePart {
        private Double temp;
        @JsonProperty("feels_like")
        private Double feelsLike;
    }

    /**
     * Wind data in m/s
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindPart {
        private Double speed;
    }

    /**
     * Solar events.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysPart {
        private Long sunrise;
        private Long sunset;
    }
}

