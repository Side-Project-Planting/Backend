package com.example.auth.presentation;

import java.util.StringJoiner;

import org.springframework.stereotype.Component;

@Component
public class CustomCookieManager {

    public Builder createBuilder(String name, String value) {
        return new Builder(name, value);
    }

    public static class Builder {
        private String name;

        private String value;

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

        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }

        public Builder secure() {
            this.secure = true;
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
