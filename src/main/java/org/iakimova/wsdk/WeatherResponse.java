package org.iakimova.wsdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO representing the "current weather" response from the OpenWeather API.
 * Maps 1:1 to the OpenWeather JSON structure using Jackson snake_case strategy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private List<WeatherCondition> weather;

    @JsonProperty("main")
    private MainData temperature;
    private Integer visibility;
    private WindData wind;

    @JsonProperty("dt")
    private Long datetime;
    private SysData sys;
    private Integer timezone;
    private String name;

    /**
     * Helper to get the first weather condition if available.
     */
    public WeatherCondition firstWeather() {
        return (weather == null || weather.isEmpty()) ? null : weather.get(0);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherCondition {
        private String main;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private Double temp;
        @JsonProperty("feels_like")
        private Double feelsLike;
        private Integer humidity;
        private Integer pressure;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindData {
        private Double speed;
        private Integer deg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysData {
        private Long sunrise;
        private Long sunset;
    }
}
