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

public class GeminiAdvisor implements WeatherAdvisor {

    private static final Logger log = LoggerFactory.getLogger(GeminiAdvisor.class);
    private final WeatherAiAgent aiAgent;

    public GeminiAdvisor(String geminiApiKey) {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Gemini API key is required");
        }

        // Оставляем проверенные настройки из V1
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-flash-latest") // Работающее название
                .logRequestsAndResponses(true)    // Оставляем для контроля
                .maxOutputTokens(1000)
                .build();

        this.aiAgent = AiServices.builder(WeatherAiAgent.class)
                .chatLanguageModel(model)
                .build();
    }

    @Override
    public String getAdvice(WeatherResponse weather) {
        if (weather == null) return "No weather data available.";

        try {
            // Оставляем твой StringBuilder — он безопаснее (защищает от NullPointerException)
            String summary = formatWeatherForAI(weather);
            log.debug("Summary sent to AI:\n{}", summary);
            return aiAgent.giveWeatherAdvice(summary);
        } catch (Exception e) {
            log.error("Gemini AI failure", e); // Логируем весь стек ошибки
            return "AI Advice is currently having issues. Please try again later.";
        }
    }

    private String formatWeatherForAI(WeatherResponse weather) {
        StringBuilder sb = new StringBuilder();
        sb.append("City: ").append(weather.getName()).append(". ");

        if (weather.getTemperature() != null) {
            // convert to Celsius
            double tempC = weather.getTemperature().getTemp() - 273.15;
            double feelsLikeC = weather.getTemperature().getFeelsLike() - 273.15;

            sb.append(String.format(java.util.Locale.US, "Temp: %.1f°C (Feels like: %.1f°C). ", tempC, feelsLikeC));
        }

        if (weather.firstWeather() != null) {
            sb.append("Condition: ").append(weather.firstWeather().getDescription()).append(". ");
        }

        if (weather.getWind() != null) {
            sb.append(String.format("Wind speed: %.1f m/s. ", weather.getWind().getSpeed()));
        }
        return sb.toString();
    }

    private interface WeatherAiAgent {
        // Берем крутой структурированный промпт из V2
        @SystemMessage({
                "You are a helpful and practical weather assistant.",
                "Based on the provided technical data, give a friendly 2-sentence advice.",
                "Tell the user what to wear and suggest one suitable activity.",
                "Keep it under 50 words and use a warm tone."
        })
        @UserMessage("Current weather summary:\n{{s}}")
        String giveWeatherAdvice(@V("s") String summary);
    }
}