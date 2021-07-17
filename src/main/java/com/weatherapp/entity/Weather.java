package com.weatherapp.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Weather {
	private String city_name;
	private List<Forecast> forecasts;

	public Weather() {
		this.forecasts = new ArrayList<Forecast>(4);
	}

}
