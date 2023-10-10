package com.example.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.auth.oauth.google.GoogleProperties;

@EnableConfigurationProperties({GoogleProperties.class})
public class ConfigurationPropertiesConfig {
}
