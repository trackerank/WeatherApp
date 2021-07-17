package com.weatherapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.weatherapp.entity.Forecast;
import com.weatherapp.entity.Weather;
import com.weatherapp.error.WeatherDataException;
import com.weatherapp.webclients.RestWebClient;

@Service
public class WeatherServiceImpl implements WeatherService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private ObjectMapper mapper;

	private Cache<String, Weather> cache;

	private String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=";
	private static final String API_KEY = System.getenv("API_KEY");
	private static final String responseType = "&mode=json&units=metric&appid=";

	public WeatherServiceImpl(@Autowired ObjectMapper mapper) {
		this.mapper = mapper;
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.cache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(100).build();
	}

	@Override
	public Weather getWeatherForCity(String city) {
		return getWeatherForCity(city, false);
	}

	@Override
	public Weather getWeatherForCity(String city, Boolean offineMode) {

		logger.info("Inside WeatherServiceImpl:getWeatherForCity()");
		Weather weather = null;

		if (offineMode) {
			weather = getWeatherForCityOffline(weather, city);
		} else {
			weather = getWeatherForCityOnline(weather, city);
		}
		logger.info("Returning WeatherServiceImpl:getWeatherForCity()");
		return weather;
	}

	public Weather getWeatherForCityOnline(Weather weather, String city) {
		logger.info("Inside getWeatherForCityOnline, will fetch real-time live data");
		try {
			RestWebClient restClient = RestWebClient.getInstance();

			if (StringUtils.isEmpty(API_KEY)) {
				logger.debug(
						"Can't extract API_KEY from environment variables, please set in system env variable or build config env variable in IDE");
			} else {
				logger.debug("API_KEY extracted successfully from environment variables");
			}
			String reqUrl = new StringBuilder(apiUrl).append(city).append(responseType).append(API_KEY).toString();
			logger.info("About to call open weather API");
			ResponseEntity<String> response = restClient.getUrl(restClient, reqUrl);
			weather = getEntityForResponse(city, response.getBody());
			this.cache.put(city, weather);
		} catch (Exception e) {
			logger.error("Exception occured in calling open weather API", e);
		}
		return weather;

	}

	private Weather getWeatherForCityOffline(Weather weather, String city) {
		logger.info("Inside getWeatherForCityOffline");

		weather = this.cache.getIfPresent(city);
		// System.out.println(weather);
		logger.info("Inside getWeatherForCityOffline, extracted cached weather details for city: " + city);

		if (null == weather) {
			logger.info("No cache found for city :" + city);
			weather = new Weather();
			weather.setCity_name(city);
			weather.setForecasts(null);
		}
		return weather;
	}

	// Data binding
	private Weather getEntityForResponse(String city, String response) {
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

	// Extract WeatherForecast data for next 3 days
	private void buildWeatherForecast(Weather weather, JsonNode forecastList) {
		Map<Integer, List<Forecast>> forecastMapper = new LinkedHashMap<Integer, List<Forecast>>();

		LocalDateTime currentDate = LocalDateTime.now();
		double wind_speed = 0;
		String weather_type = "";
		try {
			for (JsonNode entry : forecastList) {
				String dateTime = entry.path("dt_txt").asText();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
				// if the data is older than current timestamp, simply proceed to next records
				if (localDateTime.isBefore(currentDate)) {
					continue;
				}
				// extracting data only for next 3 days
				if (localDateTime.getDayOfMonth() > currentDate.getDayOfMonth() + 3) {
					break;
				}
				double temp_max = entry.path("main").path("temp_max").asDouble();
				double temp_min = entry.path("main").path("temp_min").asDouble();
				double humidity_level = entry.path("main").path("humidity").asDouble();
				JsonNode weatherList = entry.path("weather");
				if (null != weatherList && weatherList.hasNonNull(0)) {
					weather_type = weatherList.get(0).path("main").asText();
				}
				JsonNode wind_details = entry.path("wind");
				if (null != wind_details && wind_details.hasNonNull(0)) {
					wind_speed = wind_details.get(0).path("speed").asDouble();
				}
				// System.out.println("weather_main" + weather_type);

				if (forecastMapper.get(localDateTime.getDayOfMonth()) == null) {
					forecastMapper.put(localDateTime.getDayOfMonth(), new ArrayList<Forecast>());
				}
				forecastMapper.get(localDateTime.getDayOfMonth())
						.add(new Forecast(dateTime, humidity_level, temp_min, temp_max, weather_type, wind_speed, ""));

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

	// Adding required action, can be extended for new conditions
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

	@Autowired
	public void clearAllCaches() {
		logger.debug("About to clear all cache data");
		cache.invalidateAll();
		logger.debug("Success: clear all cache data");
	}
}
