package com.weatherapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weatherapp.service.CachingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("cache")
@Api(tags = { "Weather App REST endpoints for cache" })
public class CachingControllerImpl {

	@Autowired
	CachingService cachingService;

	@GetMapping("clearAll")
	@ApiOperation(value = "Clearing cached weather details", notes = "Clear all cached weather details")
	public String clearAllCaches() {
		cachingService.evictAllCaches();
		return "Successfully cleared cache values";

	}
}
