package com.example.demo.oauth;

public class GoogleOAuthProvider implements OAuthProvider {
    @Override
    public boolean match(String name) {
        return false;
    }

    @Override
    public String getAuthorizedUrl() {
        return null;
    }
}
