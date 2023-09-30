package com.example.demo.config;

import com.example.demo.oauth.GoogleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({GoogleProperties.class})
public class ConfigurationPropertiesConfig {
}
