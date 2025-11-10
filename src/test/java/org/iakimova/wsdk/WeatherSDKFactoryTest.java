package org.iakimova.wsdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WeatherSDKFactory singleton behavior.
 * API-agnostic, so we pass WeatherClient to factory.
 */
class WeatherSDKFactoryTest {

    @Test
    void testSingletonPerApiKey() throws WeatherSDKException {
        // Create stub clients with the same "API key" internally
        WeatherClient client1 = new WeatherClientStub("{\"weather\":{\"main\":\"Clear\"}}");
        WeatherClient client2 = new WeatherClientStub("{\"weather\":{\"main\":\"Clear\"}}");

        // First creation
        WeatherSDK sdk1 = WeatherSDKFactory.createSDK("KEY1", client1, Mode.ON_DEMAND, 10);

        // Second creation with the same key → should return the same instance
        WeatherSDK sdk2 = WeatherSDKFactory.createSDK("KEY1", client2, Mode.ON_DEMAND, 10);

        assertSame(sdk1, sdk2, "SDK instances with the same API key should be the same");
    }

    @Test
    void testDifferentKeysProduceDifferentInstances() throws WeatherSDKException {
        WeatherClient client1 = new WeatherClientStub("{\"weather\":{\"main\":\"Clear\"}}");
        WeatherClient client2 = new WeatherClientStub("{\"weather\":{\"main\":\"Clouds\"}}");

        WeatherSDK sdk1 = WeatherSDKFactory.createSDK("KEY1", client1, Mode.ON_DEMAND, 10);
        WeatherSDK sdk2 = WeatherSDKFactory.createSDK("KEY2", client2, Mode.ON_DEMAND, 10);

        assertNotSame(sdk1, sdk2, "SDK instances with different API keys should be different");
    }

    @Test
    void testDeleteRemovesInstance() throws WeatherSDKException {
        WeatherClient client1 = new WeatherClientStub("{\"weather\":{\"main\":\"Clear\"}}");

        WeatherSDK sdk1 = WeatherSDKFactory.createSDK("KEY3", client1, Mode.ON_DEMAND, 10);
        WeatherSDKFactory.deleteSDK("KEY3");

        WeatherSDK sdk2 = WeatherSDKFactory.createSDK("KEY3", client1, Mode.ON_DEMAND, 10);

        assertNotSame(sdk1, sdk2, "After deletion, creating SDK with the same key should produce a new instance");
    }
}
