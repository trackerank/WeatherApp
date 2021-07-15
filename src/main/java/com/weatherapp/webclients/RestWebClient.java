package com.weatherapp.webclients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestWebClient extends RestTemplate {

	static RestWebClient restWebClient = null;

	private RestWebClient() {
	}

	public static RestWebClient getInstance() {
		if (restWebClient == null) {
			restWebClient = new RestWebClient();
		}
		return restWebClient;

	}

	public ResponseEntity<String> getUrl(RestWebClient client, String url) throws RestClientException {

		ResponseEntity<String> response = client.getForEntity(url, String.class);
		return response;
	}
}
