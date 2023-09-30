package com.example.demo.oauth;

import java.util.Objects;

public class GoogleOAuthProvider implements OAuthProvider {
    private static final String NAME = "google";

    @Override
    public boolean match(String name) {
        return Objects.equals(NAME, name);
    }

    @Override
    public String getAuthorizedUrl() {
        return null;
    }
}
