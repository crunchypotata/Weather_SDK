package org.iakimova.wsdk.examples;

import org.iakimova.wsdk.*;
import org.iakimova.wsdk.domain.Mode;
import org.iakimova.wsdk.domain.WeatherSDKException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Example demonstrating Smart AI weather advice with auto-fallback.
 */
public class ExampleAIUsage {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String weatherKey = dotenv.get("OPENWEATHER_API_KEY");
        String openAiKey = dotenv.get("OPENAI_API_KEY");
        String geminiKey = dotenv.get("GEMINI_API_KEY");

        if (weatherKey == null) {
            System.err.println("Error: OPENWEATHER_API_KEY is missing in .env");
            return;
        }

        try {
            // Configure the SDK. 
            // We provide both keys. The Factory is now configured to prioritize Gemini 
            // (because it has a free tier) and fallback to OpenAI if Gemini fails.
            WeatherSDKConfig config = WeatherSDKConfig.builder()
                    .withMode(Mode.ON_DEMAND)
                    .withGeminiApiKey(geminiKey)
                    .withOpenAiApiKey(openAiKey)
                    .build();

            WeatherSDK sdk = WeatherSDKFactory.getSDK(weatherKey, config);

            String city = "London";
            System.out.println("Fetching weather and Smart AI advice for " + city + "...");

            // This will now use Gemini first.
            String advice = sdk.getAIAdvice(city);
            
            System.out.println("\n--------------------------------");
            System.out.println("SMART AI ADVICE:");
            System.out.println(advice);
            System.out.println("--------------------------------\n");

            WeatherSDKFactory.deleteSDK(weatherKey);

        } catch (WeatherSDKException e) {
            System.err.println("SDK Error: " + e.getMessage());
        }
    }
}
