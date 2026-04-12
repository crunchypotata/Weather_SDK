package org.iakimova.wsdk.advisor;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.iakimova.wsdk.domain.WeatherResponse;

import java.time.Duration;

/**
 * An adapter for {@link WeatherAdvisor} that uses LangChain4j with OpenAI.
 */
public class LangChain4jAdvisor implements WeatherAdvisor {

    private static final Logger log = LoggerFactory.getLogger(LangChain4jAdvisor.class);
    private final WeatherAiAgent aiAgent;

    public LangChain4jAdvisor(String openAiApiKey) {
        if (openAiApiKey == null || openAiApiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenAI API key is required");
        }

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName("gpt-3.5-turbo")
                .timeout(Duration.ofSeconds(20))
                .logRequests(true)
                .logResponses(true)
                .build();

        this.aiAgent = AiServices.builder(WeatherAiAgent.class)
                .chatLanguageModel(model)
                .build();
    }

    @Override
    public String getAdvice(WeatherResponse weather) {
        if (weather == null) return "No weather data.";
        
        try {
            String weatherSummary = formatWeatherForAI(weather);
            return aiAgent.giveWeatherAdvice(weatherSummary);
        } catch (Exception e) {
            log.error("AI Advice failure", e);
            return "AI Advice unavailable.";
        }
    }

    private String formatWeatherForAI(WeatherResponse weather) {
        return String.format("City: %s, Condition: %s, Temp: %.2fK, Wind: %.2fm/s",
                weather.getName(),
                (weather.firstWeather() != null ? weather.firstWeather().getMain() : "N/A"),
                (weather.getTemperature() != null ? weather.getTemperature().getTemp() : 0.0),
                (weather.getWind() != null ? weather.getWind().getSpeed() : 0.0));
    }

    private interface WeatherAiAgent {
        @SystemMessage("You are a helpful weather advisor. Provide concise advice (max 50 words) based on the conditions.")
        @UserMessage("Current weather: {{weatherSummary}}")
        String giveWeatherAdvice(@V("weatherSummary") String weatherSummary);
    }
}
