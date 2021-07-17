package com.weatherapp.service;

import com.weatherapp.entity.Weather;

public interface WeatherService {
	public Weather getWeatherForCity(String city, Boolean offineMode);

	public Weather getWeatherForCity(String city);

	public void clearAllCaches();
}
