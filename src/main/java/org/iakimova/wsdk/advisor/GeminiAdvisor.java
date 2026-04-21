package org.iakimova.wsdk.advisor;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.iakimova.wsdk.domain.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for {@link WeatherAdvisor} that uses Google Gemini AI.
 */
public class GeminiAdvisor implements WeatherAdvisor {

    private static final Logger log = LoggerFactory.getLogger(GeminiAdvisor.class);
    private final WeatherAiAgent aiAgent;

    public GeminiAdvisor(String geminiApiKey) {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Gemini API key is required");
        }

        // We switch to 'gemini-pro' as it's the most compatible model across all API versions
//        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
//                .apiKey(geminiApiKey)
//                .modelName("gemini-1.5-flash")
//                .logRequestsAndResponses(true)
////                .maxOutputTokens(150)
////                .temperature(0.5)
//                .build();

        this.aiAgent = AiServices.builder(WeatherAiAgent.class)
                .chatLanguageModel(OpenAiChatModel.builder()
                        // Убираем последний слэш и используем v1 (более стабильную)
                        .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/")
                        .apiKey(geminiApiKey)
                        .modelName("gemini-1.5-flash")
                        .logRequests(true) // Добавь это, чтобы видеть, куда именно идет запрос
                        .logResponses(true)
                        .maxTokens(150)
                        .temperature(0.5)
                        .build())
                .build();
    }

    @Override
    public String getAdvice(WeatherResponse weather) {
        if (weather == null) return "No weather data available.";
        
        try {
            String summary = String.format("City: %s, Condition: %s (%s), Temp: %.2fK, Humidity: %d%%",
                    weather.getName(),
                    (weather.firstWeather() != null ? weather.firstWeather().getMain() : "N/A"),
                    (weather.firstWeather() != null ? weather.firstWeather().getDescription() : "N/A"),
                    (weather.getTemperature() != null ? weather.getTemperature().getTemp() : 0.0),
                    (weather.getTemperature() != null ? weather.getTemperature().getHumidity() : 0));
            
            return aiAgent.giveWeatherAdvice(summary);
        } catch (Exception e) {
            log.error("Gemini AI error: {}", e.getMessage());
            return "AI Advice unavailable. Google AI returned an error: " + e.getMessage();
        }
    }

    private interface WeatherAiAgent {
        @SystemMessage("You are a helpful weather advisor. Provide concise advice (max 50 words).")
        @UserMessage("Current weather summary: {{s}}")
        String giveWeatherAdvice(@V("s") String summary);
    }
}
