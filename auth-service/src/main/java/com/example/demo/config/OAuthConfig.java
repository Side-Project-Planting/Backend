package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.oauth.OAuthProvider;
import com.example.demo.oauth.google.GoogleOAuthClient;
import com.example.demo.oauth.google.GoogleOAuthProvider;
import com.example.demo.oauth.google.GoogleProperties;

@Configuration
public class OAuthConfig {
    @Bean
    public OAuthProvider googleOAuthProvider(GoogleProperties properties, GoogleOAuthClient client) {
        return new GoogleOAuthProvider(properties, client);
    }
}
