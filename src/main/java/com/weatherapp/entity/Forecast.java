package com.weatherapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Forecast {
	private String dt_text;
	private double avg_humidity;
	private double min_temp;
	private double max_temp;
	private String weather_type;
	private double wind_speed;
	private String action;
}
