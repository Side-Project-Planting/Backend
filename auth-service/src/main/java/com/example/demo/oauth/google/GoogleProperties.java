package com.example.demo.oauth.google;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.example.demo.oauth.OAuthProperties;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleProperties implements OAuthProperties {
    private String authorizedUriEndpoint;
    private String clientId;
    private String redirectUri;
    private String[] scope;
    private String responseType;
    private String tokenUri;
    private String clientSecret;
    private String userInfoUri;
}
