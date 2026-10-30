package com.poc.client_portfolio.securityConfig;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		// checking the header
		if (header == null || header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// extracting the jwt token
		String token = header.substring(7);

		try {
			if (jwtService.isTokenValid(token)) {
				String userName = jwtService.extractUserName(token);

				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails user = userDetailsService.loadUserByUsername(userName);

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
							user.getAuthorities());
					
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

}
