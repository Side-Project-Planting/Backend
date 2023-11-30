package com.example.auth.presentation;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomCookieManager {
    private final List<CustomCookiesProperties.CookieInfo> cookieInfos;

    @Autowired
    public CustomCookieManager(CustomCookiesProperties customCookiesProperties) {
        this.cookieInfos = customCookiesProperties.getCookieInfos();
    }

    public String createRefreshToken(String refreshToken) {
        CustomCookiesProperties.CookieInfo refreshCookieInfo = getCookieInfo("refresh");

        return createBuilder("refresh", refreshToken)
            .httpOnly(refreshCookieInfo.isHttpOnly())
            .secure(refreshCookieInfo.isSecure())
            .maxAge(refreshCookieInfo.getMaxAge())
            .path(refreshCookieInfo.getPath())
            .sameSite(refreshCookieInfo.getSameSite())
            .build();
    }

    private CustomCookiesProperties.CookieInfo getCookieInfo(String name) {
        return cookieInfos.stream()
            .filter(cookieInfo -> Objects.equals(cookieInfo.getName(), name))
            .findAny()
            .orElseThrow(() -> new RuntimeException("해당되는 이름의 토큰은 만들 수 없습니다"));
    }

    public Builder createBuilder(String name, String value) {
        return new Builder(name, value);
    }

    static class Builder {
        private final String name;

        private final String value;

        private String sameSite;
        private boolean httpOnly;
        private String path;
        private Integer maxAge;
        private boolean secure;

        public Builder(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Builder sameSite(String sameSite) {
            this.sameSite = sameSite;
            return this;
        }

        public Builder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder maxAge(Integer maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public String build() {
            StringJoiner sj = new StringJoiner("; ");
            sj.add(name + "=" + value);
            if (maxAge != null) {
                sj.add("Max-Age=" + maxAge);
            }
            if (path != null) {
                sj.add("Path=" + path);
            }
            if (sameSite != null) {
                sj.add("SameSite=" + sameSite);
            }
            if (httpOnly) {
                sj.add("HttpOnly");
            }
            if (secure) {
                sj.add("Secure");
            }
            return sj.toString();
        }

    }
}
