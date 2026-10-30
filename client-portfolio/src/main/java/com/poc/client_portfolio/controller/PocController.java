package com.poc.client_portfolio.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.client_portfolio.service.PocService;

import tools.jackson.databind.JsonNode;

@RestController
public class PocController {

	private final PocService pocService;

	public PocController(PocService pocService) {
		this.pocService = pocService;
	}

	@GetMapping("/client-poc")
	public JsonNode portfolioGridData() throws InterruptedException, ExecutionException {
		return pocService.portfolioGridData();
	}
}
