package com.example.auth.presentation;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "custom-cookies")
@Getter
@Setter
@Component
public class CustomCookiesProperties {
    private List<CookieInfo> cookieInfos;

    @Getter
    @Setter
    public static class CookieInfo {
        private String name;

        private boolean httpOnly;

        private boolean secure;

        private Integer maxAge;

        private String path;

        private String sameSite;
    }
}
