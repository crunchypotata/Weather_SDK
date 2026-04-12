package org.iakimova.wsdk.advisor;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for {@link WeatherAdvisor} that uses Google Gemini AI.
 * Updated for compatibility with Google AI Studio API.
 */
public class GeminiAdvisor implements WeatherAdvisor {

    private static final Logger log = LoggerFactory.getLogger(GeminiAdvisor.class);
    private final WeatherAiAgent aiAgent;

    public GeminiAdvisor(String geminiApiKey) {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Gemini API key is required");
        }

        // Using "gemini-1.5-flash" is standard. 
        // If you still get 404, the problem is likely in the API Key permissions in Google AI Studio.
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-1.5-flash") 
                .build();

        this.aiAgent = AiServices.builder(WeatherAiAgent.class)
                .chatLanguageModel(model)
                .build();
    }

    @Override
    public String getAdvice(WeatherResponse weather) {
        if (weather == null) return "No weather data.";
        
        try {
            // Detailed weather summary for the AI
            String summary = String.format("City: %s, Condition: %s (%s), Temp: %.2fK, Humidity: %d%%, Wind: %.2fm/s",
                    weather.getName(),
                    (weather.firstWeather() != null ? weather.firstWeather().getMain() : "N/A"),
                    (weather.firstWeather() != null ? weather.firstWeather().getDescription() : "N/A"),
                    (weather.getTemperature() != null ? weather.getTemperature().getTemp() : 0.0),
                    (weather.getTemperature() != null ? weather.getTemperature().getHumidity() : 0),
                    (weather.getWind() != null ? weather.getWind().getSpeed() : 0.0));
            
            log.debug("Requesting Gemini advice for: {}", weather.getName());
            return aiAgent.giveWeatherAdvice(summary);
        } catch (Exception e) {
            log.error("Gemini AI failure: {}", e.getMessage());
            return "AI Advice via Gemini currently unavailable. (Error: " + e.getMessage() + ")";
        }
    }

    private interface WeatherAiAgent {
        @SystemMessage("You are a helpful weather advisor. Provide concise advice (max 50 words) based on the weather conditions.")
        @UserMessage("Current weather conditions:\n{{s}}")
        String giveWeatherAdvice(@V("s") String summary);
    }
}
