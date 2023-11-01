package com.example.auth.client;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "member-service")
@Getter
@Setter
public class MemberServiceProperties {
    private String baseUri;
    private String path;
}
