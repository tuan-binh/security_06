package com.ra.config;

import com.ra.security.jwt.CustomAccessDeniedHandler;
import com.ra.security.jwt.JwtEntryPoint;
import com.ra.security.jwt.JwtTokenFilter;
import com.ra.security.user_principal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	@Autowired
	private JwtEntryPoint jwtEntryPoint;
	@Autowired
	private JwtTokenFilter jwtTokenFilter;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private UserDetailService userDetailsService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				  .cors(auth -> auth.configurationSource(request -> {
					  CorsConfiguration config = new CorsConfiguration();
					  config.setAllowedOrigins(List.of("http://localhost:5173/"));
					  config.setAllowedMethods(List.of("*"));
					  config.setAllowCredentials(true);
					  config.setAllowedHeaders(List.of("*"));
					  config.setExposedHeaders(List.of("Authorization"));
					  return config;
				  }))
				  .csrf(AbstractHttpConfigurer::disable)
				  .authenticationProvider(authenticationProvider())
				  .authorizeHttpRequests((auth) ->
							 auth.requestMatchers("/auth/**","/upload/**","/**").permitAll()
										.anyRequest().authenticated())
				  .exceptionHandling((auth) ->
							 auth.authenticationEntryPoint(jwtEntryPoint)
										.accessDeniedHandler(customAccessDeniedHandler))
				  .sessionManagement((auth) -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				  .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				  .build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}
