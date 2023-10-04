package com.example.demo.oauth.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.example.demo.oauth.OAuthProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleProperties implements OAuthProperties {
    private String authorizedUriEndpoint;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String[] scope;
    private String responseType;
    private String tokenUri;
    private String userInfoUri;
}
