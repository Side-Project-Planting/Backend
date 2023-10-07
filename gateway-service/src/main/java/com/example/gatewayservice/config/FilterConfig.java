package com.example.gatewayservice.config;

import com.example.gatewayservice.filter.AuthenticationFilter;
import com.example.gatewayservice.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtValidator jwtValidator;

    @Bean
    @Order(-1)
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(jwtValidator);
    }

}
