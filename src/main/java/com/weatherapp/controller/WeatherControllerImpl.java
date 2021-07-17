package com.weatherapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.weatherapp.entity.Weather;
import com.weatherapp.error.WeatherDataException;
import com.weatherapp.error.WeatherError;
import com.weatherapp.service.WeatherService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = { "Weather App REST endpoints" })
@RequestMapping(value = "/ctrl", produces = "application/json")
public class WeatherControllerImpl {
	@Autowired
	WeatherService service;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/offline/{city}")
	@ApiOperation(value = "Gathering weather details for requested city", notes = "request data for a city")
	public Weather getWeatherInfoOffline(@PathVariable("city") String city) {
		// System.out.println("Search for City : " + city);
		logger.info("Search for City in offline mode: " + city);
		return service.getWeatherForCity(city, true);
	}

	@GetMapping("/{city}")
	@ApiOperation(value = "Gathering weather details for requested city", notes = "request data for a city")
	public Weather getWeatherInfo(@PathVariable("city") String city) {
		// System.out.println("Search for City : " + city);
		logger.info("Search for City : " + city);
		return service.getWeatherForCity(city);
	}

	@GetMapping("/")
	@ApiOperation(value = "Gathering weather details for default city", notes = "request data for default city")
	public Weather getWeatherInfo() {
		// Hardcoded value to a particular city in case of no value entered by user
		return getWeatherInfo("Delhi");
	}

	@ExceptionHandler
	public ResponseEntity<WeatherError> handleException(Exception e) {
		WeatherError error = new WeatherError();
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		error.setMessage(e.getMessage());
		logger.error("HTTP 500 error : " + e.getMessage(), e);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<WeatherError> handleException(WeatherDataException e) {
		WeatherError error = new WeatherError();
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		error.setMessage(e.getMessage());
		logger.error("HTTP 500 error : " + e.getMessage(), e);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<WeatherError> handleException(RestClientException e) {
		WeatherError error = new WeatherError();
		error.setStatus(HttpStatus.BAD_REQUEST);
		error.setMessage(e.getMessage());
		logger.error("HTTP 400 error : " + e.getMessage(), e);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
