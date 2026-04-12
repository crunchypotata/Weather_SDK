package org.iakimova.wsdk.advisor;

import org.iakimova.wsdk.domain.WeatherResponse;

/**
 * Port for providing AI-based weather advice.
 * <p>
 * Implementations should analyze {@link WeatherResponse} and generate
 * human-readable suggestions (e.g., what to wear or carry).
 */
public interface WeatherAdvisor {
    /**
     * Generates advice based on the provided weather data.
     *
     * @param weather The current weather data.
     * @return A string containing the AI's advice.
     */
    String getAdvice(WeatherResponse weather);
}
