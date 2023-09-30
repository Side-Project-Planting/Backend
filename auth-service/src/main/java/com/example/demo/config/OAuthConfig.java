package com.example.demo.config;

import com.example.demo.oauth.google.GoogleOAuthProvider;
import com.example.demo.oauth.google.GoogleProperties;
import com.example.demo.oauth.OAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthConfig {
    @Bean
    public OAuthProvider googleOAuthProvider(GoogleProperties properties) {
        return new GoogleOAuthProvider(properties);
    }
}
