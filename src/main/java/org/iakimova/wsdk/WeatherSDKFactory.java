package org.iakimova.wsdk;

import org.iakimova.wsdk.advisor.GeminiAdvisor;
import org.iakimova.wsdk.advisor.LangChain4jAdvisor;
import org.iakimova.wsdk.advisor.WeatherAdvisor;
import org.iakimova.wsdk.cache.LRUWeatherCache;
import org.iakimova.wsdk.client.WeatherApiClient;
import org.iakimova.wsdk.client.WeatherClient;
import org.iakimova.wsdk.client.WeatherJsonMapper;
import org.iakimova.wsdk.core.WeatherSDKImpl;
import org.iakimova.wsdk.domain.WeatherResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton factory for managing WeatherSDK instances.
 * <p>
 * This is the Composition Root of our Hexagonal Architecture. 
 * It wires adapters (Client, Cache, Advisor) to the Core (SDKImpl).
 */
public final class WeatherSDKFactory {

    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    private WeatherSDKFactory() {}

    /**
     * Creates or retrieves an existing SDK instance for the given API key.
     * Uses default configuration.
     *
     * @param apiKey OpenWeather API key
     * @return Fully initialized WeatherSDK
     */
    public static WeatherSDK getSDK(String apiKey) {
        return getSDK(apiKey, WeatherSDKConfig.builder().build());
    }

    /**
     * Creates or retrieves an existing SDK instance with custom configuration.
     */
    public static WeatherSDK getSDK(String apiKey, WeatherSDKConfig config) {
        WeatherClient client = new WeatherApiClient(apiKey, new WeatherJsonMapper());
        return getSDK(apiKey, config, client);
    }

    /**
     * Advanced: Creates or retrieves an existing SDK instance with a custom {@link WeatherClient}.
     */
    public static WeatherSDK getSDK(String apiKey, WeatherSDKConfig config, WeatherClient client) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        return instances.computeIfAbsent(apiKey, key -> {
            LRUWeatherCache cache = new LRUWeatherCache(config.getCacheSize(), config.getCacheTtlMillis());
            
            // Create the smart AI advisor based on provided keys
            WeatherAdvisor advisor = createAdvisor(config);

            return new WeatherSDKImpl(
                    client,
                    config.getMode(),
                    config.getPollingIntervalMinutes(),
                    cache,
                    advisor
            );
        });
    }

    /**
     * Internal logic to decide which AI provider to use.
     * Priority: Gemini (Free) -> OpenAI.
     */
    private static WeatherAdvisor createAdvisor(WeatherSDKConfig config) {
        String geminiKey = config.getGeminiApiKey();
        String openAiKey = config.getOpenAiApiKey();

        // We can implement a composite advisor with fallback here
        if (geminiKey != null && !geminiKey.trim().isEmpty() && 
            openAiKey != null && !openAiKey.trim().isEmpty()) {
            
            return new WeatherAdvisor() {
                private final WeatherAdvisor primary = new GeminiAdvisor(geminiKey);
                private final WeatherAdvisor secondary = new LangChain4jAdvisor(openAiKey);

                @Override
                public String getAdvice(WeatherResponse weather) {
                    String advice = primary.getAdvice(weather);
                    if (advice == null || advice.contains("unavailable")) {
                        return secondary.getAdvice(weather);
                    }
                    return advice;
                }
            };
        }

        if (geminiKey != null && !geminiKey.trim().isEmpty()) {
            return new GeminiAdvisor(geminiKey);
        }
        
        if (openAiKey != null && !openAiKey.trim().isEmpty()) {
            return new LangChain4jAdvisor(openAiKey);
        }

        return null;
    }

    /**
     * Removes an SDK instance and cleans up resources.
     */
    public static void deleteSDK(String apiKey) {
        if (apiKey == null) return;
        WeatherSDK sdk = instances.remove(apiKey);
        if (sdk != null) {
            sdk.delete();
        }
    }
}
