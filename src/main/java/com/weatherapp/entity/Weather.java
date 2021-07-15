package com.weatherapp.entity;

import java.util.ArrayList;
import java.util.List;

public class Weather {
	private String city_name;
	private List<Forecast> forecasts;

	public Weather() {
		this.forecasts = new ArrayList<Forecast>(4);
		// final long id = forecasts.getId();
		// add(linkTo(WeatherControllerImpl.class).withSelfRel());

	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public List<Forecast> getForecasts() {
		return forecasts;
	}

	public void setForecasts(List<Forecast> forecasts) {
		this.forecasts = forecasts;
	}

}
