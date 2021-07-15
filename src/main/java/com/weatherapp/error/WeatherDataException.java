package com.weatherapp.error;

public class WeatherDataException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WeatherDataException(String message) {
		super(message);
	}
}
