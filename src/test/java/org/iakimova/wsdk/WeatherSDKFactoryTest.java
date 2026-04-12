package org.iakimova.wsdk;

import org.iakimova.wsdk.client.WeatherClient;
import org.iakimova.wsdk.domain.WeatherSDKException;
import org.junit.jupiter.api.Test;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WeatherSDKFactory singleton behavior.
 */
class WeatherSDKFactoryTest {

    private final Supplier<WeatherResponse> responseSupplier = () -> 
            WeatherResponse.builder().name("TestCity").build();

    @Test
    void testSingletonPerApiKey() throws WeatherSDKException {
        // Create stub clients
        WeatherClient client1 = new WeatherClientStub(responseSupplier);
        WeatherClient client2 = new WeatherClientStub(responseSupplier);
        
        WeatherSDKConfig config = WeatherSDKConfig.builder().build();

        // First creation
        WeatherSDK sdk1 = WeatherSDKFactory.getSDK("KEY1", config, client1);

        // Second creation with the same key → should return the same instance
        WeatherSDK sdk2 = WeatherSDKFactory.getSDK("KEY1", config, client2);

        assertSame(sdk1, sdk2, "SDK instances with the same API key should be the same");
    }

    @Test
    void testDifferentKeysProduceDifferentInstances() throws WeatherSDKException {
        WeatherClient client = new WeatherClientStub(responseSupplier);
        WeatherSDKConfig config = WeatherSDKConfig.builder().build();

        WeatherSDK sdk1 = WeatherSDKFactory.getSDK("KEY_A", config, client);
        WeatherSDK sdk2 = WeatherSDKFactory.getSDK("KEY_B", config, client);

        assertNotSame(sdk1, sdk2, "SDK instances with different API keys should be different");
    }

    @Test
    void testDeleteRemovesInstance() throws WeatherSDKException {
        WeatherClient client = new WeatherClientStub(responseSupplier);
        WeatherSDKConfig config = WeatherSDKConfig.builder().build();

        WeatherSDK sdk1 = WeatherSDKFactory.getSDK("KEY_DELETE", config, client);
        WeatherSDKFactory.deleteSDK("KEY_DELETE");

        WeatherSDK sdk2 = WeatherSDKFactory.getSDK("KEY_DELETE", config, client);

        assertNotSame(sdk1, sdk2, "After deletion, creating SDK with the same key should produce a new instance");
    }
}
