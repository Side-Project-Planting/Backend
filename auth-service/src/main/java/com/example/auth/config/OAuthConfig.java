package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.auth.oauth.OAuthClient;
import com.example.auth.oauth.OAuthProperties;
import com.example.auth.oauth.OAuthProvider;
import com.example.auth.oauth.google.GoogleOAuthClient;
import com.example.auth.oauth.google.GoogleOAuthProvider;
import com.example.auth.oauth.google.GoogleProperties;

@Configuration
public class OAuthConfig {
    @Bean
    public OAuthProvider googleOAuthProvider(OAuthProperties googleProperties, OAuthClient googleOAuthClient) {
        return new GoogleOAuthProvider(googleProperties, googleOAuthClient);
    }

    @Bean
    public OAuthProperties googleProperties() {
        return new GoogleProperties();
    }

    @Bean
    public OAuthClient googleOAuthClient(OAuthProperties properties) {
        return new GoogleOAuthClient(properties);
    }
}
