package com.example.demo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.demo.oauth.google.GoogleProperties;

@EnableConfigurationProperties({GoogleProperties.class})
public class ConfigurationPropertiesConfig {
}
