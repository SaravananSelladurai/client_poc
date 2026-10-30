package com.poc.client_portfolio.securityConfig;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authManager;
	private final JwtService jwtService;

	public AuthController(AuthenticationManager authManager, JwtService jwtService) {
		this.authManager = authManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody LoginRequest request) {
		Map<String, String> responseMap = new HashMap<>();
		
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		
		String token = jwtService.generateToken(request.username());
		
		responseMap.put("accessToken", token);
		
		return responseMap;
	}

	public record LoginRequest(String username, String password) {
	}
}
