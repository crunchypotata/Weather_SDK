package org.iakimova.wsdk;

/**
 * Custom exception used by WeatherSDK.
 * Contains user-friendly message explaining reason of failure.
 */
public class WeatherSDKException extends Exception {

    public WeatherSDKException(String message) {
        super(message);
    }

    public WeatherSDKException(String message, Throwable cause) {
        super(message, cause);
    }
}
