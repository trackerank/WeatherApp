package com.weatherapp.entity;

public class Forecast {
	private String action;
	private int avg_humidity;
	private double max_temp;
	private String dt_text;
	private String weather_type;

	public Forecast(String dt_text, int avg_humidity, double max_temp, String weather_type) {
		this.dt_text = dt_text;
		this.avg_humidity = avg_humidity;
		this.max_temp = max_temp;
		this.weather_type = weather_type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getAvg_humidity() {
		return avg_humidity;
	}

	public void setAvg_humidity(int avg_humidity) {
		this.avg_humidity = avg_humidity;
	}

	public double getMax_temp() {
		return max_temp;
	}

	public void setMax_temp(double max_temp) {
		this.max_temp = max_temp;
	}

	public String getDt_text() {
		return dt_text;
	}

	public void setDt_text(String dt_text) {
		this.dt_text = dt_text;
	}

	public String getWeather_type() {
		return weather_type;
	}

	public void setWeather_type(String weather_type) {
		this.weather_type = weather_type;
	}

}
