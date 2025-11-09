# Weather SDK

Develop a SDK for accessing a weather API
Task reference: https://openweathermap.org/api

## What this SDK provides

- Accepts API KEY on initialization
- Method `getWeather(cityName)` returns current weather (first match)
- Returns weather in **normalized DTO** (structure exactly as required in the task)
- Caches weather of up to **10 cities**
- Cache TTL = **10 minutes**
- Two modes:
  - `ON_DEMAND` — requests OpenWeather only on method call
  - `POLLING` — background refresh every N minutes (**default = 10**)
- Polling interval is configurable
- All errors are returned as `WeatherSDKException`
- Only one SDK instance allowed per API key (via `WeatherSDKFactory`)
- Factory can delete SDK instance (`deleteSDK(apiKey)`)
- Logging via SLF4J (debug = cache HIT / MISS, info = lifecycle)

## SDK API

### WeatherSDK interface

| Method | Description |
|--------|-------------|
|`WeatherResponse getWeather(String city)` | Returns current weather for the given city (first match). Updates cache depending on SDK mode. |
|`void delete()` | Clears cache and stops polling (if any). |

### WeatherResponse DTO

```java
public class WeatherResponse {
    private WeatherCondition weather;
    private TemperaturePart temperature;
    private Integer visibility;
    private WindPart wind;
    private Long datetime;
    private SysPart sys;
    private Integer timezone;
    private String name;

    public static class WeatherCondition {
        private String main;
        private String description;
    }
    public static class TemperaturePart {
        private Double temp;
        private Double feelsLike;
    }
    public static class WindPart {
        private Double speed;
    }
    public static class SysPart {
        private Long sunrise;
        private Long sunset;
    }
}
```
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
WeatherSDK sdk = WeatherSDKFactory.createSDK("YOUR_API_KEY", Mode.ON_DEMAND);
WeatherResponse resp = sdk.getWeather("Barcelona");

System.out.println(resp.getName());
System.out.println("Temperature = " + resp.getTemperature().getTemp());

WeatherSDKFactory.deleteSDK("YOUR_API_KEY"); // optional cleanup
```

## Notes

**API contract (WeatherResponse):**

- fields always non-null (if provided by OpenWeather)
- temperature fields are in Kelvin (same as original API)
- datetime = unix timestamp (sec)
- timezone = offset in seconds
- wind speed m/s

## Future work (beyond the task scope)

- add request metrics and cache hit ratio
- add retry strategy on network errors
- extract HTTP client to interface for easier testing / replacing
- publish artifact to Maven Central
