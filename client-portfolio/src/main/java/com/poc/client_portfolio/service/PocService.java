package com.poc.client_portfolio.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poc.client_portfolio.config.ExternalApiService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Service
public class PocService {

	@Value("${external.mock.username}")
	private String userName;

	@Value("${external.mock.password}")
	private String password;

	private ExternalApiService externalApiService;

	private static final ObjectMapper mapper = new ObjectMapper();

	public PocService(ExternalApiService externalApiService) {
		this.externalApiService = externalApiService;
	}

	public JsonNode portfolioGridData() throws InterruptedException, ExecutionException {

		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			Future<?> cashResponse = executor.submit(() -> callExternalService("cash"));
			Future<?> etoResponse = executor.submit(() -> callExternalService("eto"));
			Future<?> equityResponse = executor.submit(() -> callExternalService("equity"));
			Future<?> internationalResponse = executor.submit(() -> callExternalService("international"));

			ArrayNode result = mapper.createArrayNode();

			result.add((JsonNode) (cashResponse.get()));
			result.add((JsonNode) (etoResponse.get()));
			result.add((JsonNode) (equityResponse.get()));
			result.add((JsonNode) (internationalResponse.get()));

			return result;
		}

	}

	private Object callExternalService(String endPoint) {
		String baseUrl = "http://localhost:8082/";
		return externalApiService.getRequest(baseUrl, endPoint, userName, password) .map(list -> mapper.valueToTree(list)).block();
	}
}
