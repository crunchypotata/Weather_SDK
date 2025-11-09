# weather-sdk-java

Small Java SDK for OpenWeather API.

Task reference: https://openweathermap.org/api

---

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

---

## Usage example

```java
WeatherSDK sdk = WeatherSDKFactory.createSDK("YOUR_API_KEY", Mode.ON_DEMAND);
WeatherResponse resp = sdk.getWeather("Barcelona");

System.out.println(resp.getName());
System.out.println("Temperature = " + resp.getTemperature().getTemp());

WeatherSDKFactory.deleteSDK("YOUR_API_KEY"); // optional cleanup
```


## How to build

./gradlew build

## Documentation

./gradlew javadoc

## Notes

API contract (WeatherResponse):
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
