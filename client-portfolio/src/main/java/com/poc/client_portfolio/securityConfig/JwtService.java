package com.poc.client_portfolio.securityConfig;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${jwt.secret.key}")
	private String secret;
	
	
	/*
	 * whenever the user first time try to login a new JWT token will be generated
	 * 
	 * this method will be invoked
	 * 
	 */
	public String generateToken(String userName) {
		SecretKey key = getSigningKey();
		
		//jwt token generate here based on username and secret key
		//jwt token contains expiration time and details
		return Jwts.builder()
			.subject(userName)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + 15 *60*1000))
			.signWith(key)
			.compact();
	}
	
	/**
	 * Checking the whether the token is valid or not
	 * 
	 * @param token
	 * @return
	 */
	public boolean isTokenValid(String token) {
		try {
			praseClaims(token);
			return true;
		} catch(JwtException | IllegalArgumentException ex) {
			return false;
		}
	}
	
	public String extractUserName(String token) {
		return praseClaims(token).getSubject();
	}
	
	
	private Claims praseClaims(String token) {
		return Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
	}
	
	
	
	private SecretKey getSigningKey() {
		 byte[] keyBytes = Decoders.BASE64.decode(secret);
		 return Keys.hmacShaKeyFor(keyBytes);
	}
}	
