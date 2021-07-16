package com.weatherapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.weatherapp.entity.Weather;
import com.weatherapp.entity.Forecast;
import com.weatherapp.error.WeatherDataException;
import com.weatherapp.webclients.RestWebClient;

@Service
public class WeatherServiceImpl implements WeatherService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=";
	private static final String API_KEY = System.getenv("API_KEY");
	private static final String responseType = "&mode=json&units=metric&appid=";

	private ObjectMapper mapper;

	public WeatherServiceImpl(@Autowired ObjectMapper mapper) {
		this.mapper = mapper;
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Override
	@Cacheable("Weather")
	public Weather getWeatherForCity(String city) {

		System.out.println("Inside WeatherServiceImpl:getWeatherForCity()");
		logger.info("Inside WeatherServiceImpl:getWeatherForCity()");
		Weather weather = new Weather();
		try {
			RestWebClient restClient = RestWebClient.getInstance();

			if (StringUtils.isEmpty(API_KEY)) {
				logger.debug("Can't extract API_KEY from environment variables");
				System.out.println("Can't extract API_KEY from environment variables");
			} else {
				logger.debug("API_KEY extracted succesfully from environment variables");
				System.out.println("API_KEY extracted succesfully from environment variables");
			}
			String reqUrl = new StringBuilder(apiUrl).append(city).append(responseType).append(API_KEY).toString();
			System.out.println("About to call open weather API");
			logger.info("About to call open weather API");
			ResponseEntity<String> response = restClient.getUrl(restClient, reqUrl);
			weather = getEntityForResponse(response.getBody(), mapper);
		} catch (Exception e) {
			logger.error("Exception occured in calling open weather API", e);
			System.out.println("Exception occured in calling open weather API -" + e.getMessage());
		}
		System.out.println("Returning WeatherServiceImpl:getWeatherForCity()");
		logger.info("Returning WeatherServiceImpl:getWeatherForCity()");
		return weather;
	}

	// Data binding
	private Weather getEntityForResponse(String response, ObjectMapper mapper) {

		Weather weather = new Weather();
		try {
			JsonNode root = mapper.readTree(response);
			weather.setCity_name(root.path("city").path("name").asText());

			JsonNode forecastList = root.path("list");
			buildWeatherForecast(weather, forecastList);

		} catch (IOException e) {
			throw new WeatherDataException(e.getMessage());
		}
		return weather;
	}

	private void buildWeatherForecast(Weather weather, JsonNode forecastList) {
		Map<Integer, List<Forecast>> forecastMapper = new LinkedHashMap<Integer, List<Forecast>>();

		LocalDateTime currentDate = LocalDateTime.now();

		try {
			for (JsonNode entry : forecastList) {
				String dateTime = entry.path("dt_txt").asText();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
				// if the data is old than current timestamp, simply proceed to next records
				if (localDateTime.isBefore(currentDate)) {
					continue;
				}
				if (localDateTime.getDayOfMonth() > currentDate.getDayOfMonth() + 3) {
					break;
				}
				int temp_max = entry.path("main").path("temp_max").asInt();
				int humidity_level = entry.path("main").path("humidity").asInt();
				JsonNode weatherList = entry.path("weather");
				String weather_type = weatherList.get(0).path("main").asText();
				// System.out.println("weather_main" + weather_type);

				if (forecastMapper.get(localDateTime.getDayOfMonth()) == null) {
					forecastMapper.put(localDateTime.getDayOfMonth(), new ArrayList<Forecast>());
				}
				forecastMapper.get(localDateTime.getDayOfMonth())
						.add(new Forecast(dateTime, humidity_level, temp_max, weather_type));

			}
		} catch (Exception e) {
			throw new WeatherDataException(e.getMessage());
		}

		// Now identify the action for each forcast
		for (Integer index : forecastMapper.keySet()) {
			List<Forecast> dayForecastList = forecastMapper.get(index);
			dayForecastList.forEach(element -> forecastFilter(weather, element));
		}
	}

	private void forecastFilter(Weather weather, Forecast forecast) {
		if (forecast.getMax_temp() > 40) {
			forecast.setAction("Use sunscreen lotion");
		} else if (forecast.getWeather_type().equalsIgnoreCase("Rain")) {
			forecast.setAction("Carry umbrella");
		} else {
			forecast.setAction("No Action. Normal day");
		}
		weather.getForecasts().add(forecast);
	}
}
