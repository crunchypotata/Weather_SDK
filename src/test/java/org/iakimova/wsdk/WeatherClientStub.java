package org.iakimova.wsdk;

import java.util.function.Supplier;

/**
 * Enhanced stub for WeatherClient.
 * Can return new instances of WeatherResponse to correctly test cache refresh.
 */
public class WeatherClientStub implements WeatherClient {

    private final Supplier<WeatherResponse> responseSupplier;

    /**
     * Creates a stub that always returns a NEW object produced by the supplier.
     */
    public WeatherClientStub(Supplier<WeatherResponse> responseSupplier) {
        this.responseSupplier = responseSupplier;
    }

    /**
     * Legacy constructor for simple cases.
     */
    public WeatherClientStub(WeatherResponse fixedResponse) {
        this.responseSupplier = () -> fixedResponse;
    }

    @Override
    public WeatherResponse getWeather(String city) {
        return responseSupplier.get();
    }
}
