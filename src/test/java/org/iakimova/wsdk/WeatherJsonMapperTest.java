package org.iakimova.wsdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WeatherJsonMapperTest {

    private WeatherJsonMapper mapper;
    private String rawJson;
    private WeatherResponse response;

    @BeforeEach
    void setUp() throws WeatherSDKException {
        mapper = new WeatherJsonMapper();
        rawJson = """
        {
          "weather": [
            {
              "main": "Clouds",
              "description": "scattered clouds"
            }
          ],
          "main": {
            "temp": 269.6,
            "feels_like": 267.57
          },
          "visibility": 10000,
          "wind": {
            "speed": 1.38
          },
          "datetime": 1675744800,
          "sys": {
            "sunrise": 1675751262,
            "sunset": 1675787560
          },
          "timezone": 3600,
          "name": "Zocca"
        }
        """;
        response = mapper.map(rawJson);
    }

    @Test
    void testWeatherFields() {
        assertNotNull(response.getWeather());
        assertNotNull(response.firstWeather());
        assertEquals("Clouds", response.firstWeather().getMain());
        assertEquals("scattered clouds", response.firstWeather().getDescription());
    }

    @Test
    void testTemperatureFields() {
        assertNotNull(response.getTemperature());
        assertEquals(269.6, response.getTemperature().getTemp());
        assertEquals(267.57, response.getTemperature().getFeelsLike());
    }

    @Test
    void testVisibility() {
        assertNotNull(response.getVisibility());
        assertEquals(10000, response.getVisibility());
    }

    @Test
    void testWind() {
        assertNotNull(response.getWind());
        assertEquals(1.38, response.getWind().getSpeed());
    }

    @Test
    void testDatetime() {
        assertNotNull(response.getDatetime());
        assertEquals(1675744800, response.getDatetime());
    }

    @Test
    void testSys() {
        assertNotNull(response.getSys());
        assertEquals(1675751262, response.getSys().getSunrise());
        assertEquals(1675787560, response.getSys().getSunset());
    }

    @Test
    void testTimezone() {
        assertNotNull(response.getTimezone());
        assertEquals(3600, response.getTimezone());
    }

    @Test
    void testName() {
        assertNotNull(response.getName());
        assertEquals("Zocca", response.getName());
    }
}
