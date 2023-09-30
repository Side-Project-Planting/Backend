package com.example.demo.oauth;

public interface OAuthProvider {
    boolean match(String name);

    String getAuthorizedUrl();
}
