# Weather SDK for Java

A lightweight, thread-safe library for integrating weather data and **AI-powered** meteorological insights into Java applications. 

Built on **Hexagonal Architecture** principles, the SDK ensures high modularity, testability, and seamless integration.

## Core Principles
*   **Decoupling**: Core business logic is isolated from infrastructure (HTTP clients, JSON parsers).
*   **Resource Efficiency**: Uses a shared connection pool to prevent socket exhaustion.
*   **Extensibility**: Support for multiple AI providers (OpenAI, Google Gemini) and customizable caching strategies.
*   **Thread Safety**: Fully compatible with high-concurrency environments.

---

## Architecture Overview
The SDK follows the **Ports and Adapters** pattern:
*   **Domain Layer**: Contains the core logic and models (`WeatherResponse`, `Mode`).
*   **Inbound Port**: The `WeatherSDK` interface serves as the primary entry point.
*   **Outbound Ports**: Abstractions for API communication (`WeatherClient`), Caching (`WeatherCache`), and AI analysis (`WeatherAdvisor`).
*   **Adapters**: Pre-built implementations for OpenWeather API, LRU-based caching, and LangChain4j-based AI advisors.

---

## Features

### 1. Operation Modes
*   **ON_DEMAND**: Fetches data only when requested by the caller.
*   **POLLING**: Periodically updates the cache in the background, ensuring near-zero latency for weather requests.

### 2. Intelligent Caching
The SDK includes a built-in LRU cache with configurable **Time-To-Live (TTL)**. Expiration logic is handled internally by the cache layer to maintain high performance.

### 3. AI Weather Advisor
Integrates with **OpenAI** and **Google Gemini** via LangChain4j to provide human-readable advice based on real-time weather metrics (e.g., clothing suggestions, activity planning).

---

## Configuration

The SDK is configured via a **Fluent Builder** API, eliminating hardcoded values and allowing precise control over its behavior.

```java
WeatherSDKConfig config = WeatherSDKConfig.builder()
    .withMode(Mode.POLLING)
    .withPollingInterval(5)                      // minutes
    .withCacheTtl(10, TimeUnit.MINUTES)          // entry expiration
    .withCacheSize(100)                          // max cities in cache
    .withGeminiApiKey(dotenv.get("GEMINI_KEY"))  // enabled AI capabilities
    .build();
```

---

## Usage

### Initialization

```java
// Uses singleton-per-API-key pattern internally
WeatherSDK sdk = WeatherSDKFactory.getSDK(WEATHER_API_KEY, config);
```

### Retrieving Weather Data

```java
WeatherResponse weather = sdk.getWeather("Barcelona");
System.out.println("City: " + weather.getName());
System.out.println("Temperature: " + weather.getTemperature().getTemp() + "K");
```

### Generating AI Advice

```java
String advice = sdk.getAIAdvice("Barcelona");
System.out.println("Meteorological Advice: " + advice);
```

---

## Build & Documentation

```bash
# Build the project
./gradlew build

# Generate Javadoc documentation
./gradlew javadoc
```

---

## Data Model (WeatherResponse)
The SDK provides a comprehensive DTO containing:
*   **Conditions**: Main status and detailed description.
*   **Metrics**: Temperature (Kelvin), Humidity (%), Pressure (hPa).
*   **Wind**: Speed (m/s) and Direction (degrees).
*   **Metadata**: Visibility, Sunrise/Sunset (Unix timestamp), and Timezone offset.

---

## Requirements
*   **Java**: 21+
*   **Dependencies**: LangChain4j (optional AI features), OkHttp 4, Jackson.

---

### Future Enhancements

*   [ ] Request metrics and cache hit ratio reporting.
*   [ ] Implement retry strategy for network errors.
*   [ ] Publish SDK artifact to Maven Central.
*   [ ] **Cache AI advice**: Implement caching for AI-generated advice to reduce API calls to paid LLMs.
