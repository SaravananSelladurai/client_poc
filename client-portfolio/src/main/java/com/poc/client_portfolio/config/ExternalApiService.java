package com.poc.client_portfolio.config;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {
	private final WebClient webClient;

	public ExternalApiService(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<?> getRequest(String baseUrl, String endPoint, String username, String password) {
		return webClient.get().uri(baseUrl+endPoint).headers(headers -> headers.setBasicAuth(username, password)).retrieve()
				.bodyToMono(Object.class);
	}
}
