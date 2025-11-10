package org.iakimova.wsdk;

/**
 * Simple stub for WeatherClient, returns a fixed JSON.
 * Used in SDK integration tests.
 */
public class WeatherClientStub implements WeatherClient {

    private final String fixedJson;

    public WeatherClientStub(String fixedJson) {
        this.fixedJson = fixedJson;
    }

    @Override
    public String getRawWeatherJson(String city) {
        // Simply return the predefined JSON
        return fixedJson;
    }
}
