package com.example.demo.config;

import com.example.demo.oauth.GoogleOAuthProvider;
import com.example.demo.oauth.GoogleProperties;
import com.example.demo.oauth.OAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthConfig {
    @Bean
    public OAuthProvider googleOAuthProvider() {
        return new GoogleOAuthProvider(new GoogleProperties());
    }
}
