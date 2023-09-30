package com.example.demo.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleProperties {
    private String authorizedUriEndpoint;
    private String clientId;
    private String redirectUri;
    private String[] scope;
    private String responseType;
    private String tokenUri;
    private String clientSecret;
}