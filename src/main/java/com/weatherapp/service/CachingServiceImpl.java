package com.weatherapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CachingServiceImpl implements CachingService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CacheManager cacheManager;

	public void evictAllCaches() {
		logger.debug("About to clear all cache data");
		cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
		logger.debug("Success: clear all cache data");
	}
}
