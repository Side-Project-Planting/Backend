package com.example.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.auth.oauth.google.GoogleProperties;
import com.example.auth.presentation.CustomCookiesProperties;

@EnableConfigurationProperties({GoogleProperties.class, CustomCookiesProperties.class})
public class ConfigurationPropertiesConfig {
}
