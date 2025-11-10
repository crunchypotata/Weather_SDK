# Weather SDK

Develop a SDK for accessing a weather API  
Task reference: https://openweathermap.org/api

## Architecture & Modularity

This SDK is **modular**, with a clear separation of concerns:

- **WeatherAPI** — handles HTTP requests to OpenWeather; can be replaced or mocked for testing.
- **WeatherCache** — independent cache for up to configurable number of cities; TTL configurable; can be swapped with custom implementations.
- **WeatherSDK** — orchestrates API calls and cache, supports **ON_DEMAND** and **POLLING** modes.

All modules are fully **configurable** and **testable** independently.

## Testing & Coverage


- Unit tests cover all **DTO mappings**, caching behavior, and SDK lifecycle.
- Integration tests simulate full SDK usage, including **background polling** and cache expiry.
- Coverage includes full lifecycle of SDK instances (creation, API calls, caching, deletion).
- Ensures that **WeatherSDKFactory** enforces **singleton-per-API-key** rule.

## Configuration & Usage
- Cache:
Size configurable (default: 10 cities)
TTL configurable (default: 10 minutes)
- Polling interval configurable for POLLING mode
- 
## SDK API
### WeatherSDK Interface

| Method | Description |
|--------|-------------|
| `WeatherResponse getWeather(String city)` | Returns current weather for the given city (first match). Updates cache depending on SDK mode (**ON_DEMAND** or **POLLING**). |
| `void delete()` | Clears cache and stops polling (if any). |
| `setCacheSize(int size)` | Configures cache size (optional). |
| `setCacheTTLMinutes(int minutes)` | Configures cache TTL (optional). |
| `setPollingIntervalMinutes(int minutes)` | Configures polling interval for **POLLING** mode (optional). |

### WeatherResponse DTO

Represents current weather returned by the SDK. Includes:

- `weather` — list of weather conditions (first element is primary)
- `temperature` — temperature info (`temp`, `feelsLike`)
- `visibility` — in meters
- `wind` — wind speed in m/s
- `datetime` — unix timestamp
- `sys` — sunrise and sunset times
- `timezone` — offset in seconds
- `name` — city name

Convenience method `firstWeather()` returns the first weather condition.

### Modes

- ON_DEMAND — updates weather only on method call
- POLLING — updates weather in background every N minutes (default 10)

### Factory

- WeatherSDKFactory.createSDK(String apiKey, Mode mode) — creates SDK instance (only one per API key)
- WeatherSDKFactory.deleteSDK(String apiKey) — deletes SDK instance

## How to build

``` bash 
    ./gradlew build
```

## Documentation

``` bash
    ./gradlew javadoc
```

## Usage example

```java
WeatherSDK sdk = WeatherSDKFactory.createSDK("YOUR_API_KEY", Mode.POLLING);

// Configure cache & polling if needed
sdk.setCacheSize(15);
sdk.setCacheTTLMinutes(5);
sdk.setPollingIntervalMinutes(2);

// Get weather for a city
WeatherResponse resp = sdk.getWeather("Barcelona");
System.out.println(resp.getName());
        System.out.println("Temperature = " + resp.getTemperature().getTemp() + " K");

// Delete SDK instance and clean up resources
        WeatherSDKFactory.deleteSDK("YOUR_API_KEY");
```

## Notes

- fields always non-null (if provided by OpenWeather)
- temperature fields are in Kelvin (same as original API)
- datetime = unix timestamp (sec)
- timezone = offset in seconds
- wind speed m/s

## Future Enhancements
- Add request metrics and cache hit ratio reporting
- Implement retry strategy on network errors
- Extract HTTP client into an interface for easier testing and replacement
- Publish SDK artifact to Maven Central
