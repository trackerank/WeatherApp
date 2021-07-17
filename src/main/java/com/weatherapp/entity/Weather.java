package com.weatherapp.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {
	private String city_name;
	private List<Forecast> forecasts;

	public Weather() {
		this.forecasts = new ArrayList<Forecast>(4);
	}

}
