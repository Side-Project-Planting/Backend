package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.oauth.OAuthClient;
import com.example.demo.oauth.OAuthProperties;
import com.example.demo.oauth.OAuthProvider;
import com.example.demo.oauth.google.GoogleOAuthClient;
import com.example.demo.oauth.google.GoogleOAuthProvider;
import com.example.demo.oauth.google.GoogleProperties;

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
