package com.example.demo.oauth;

public interface OAuthProperties {
    String getAuthorizedUriEndpoint();

    String getClientId();

    String getRedirectUri();

    String[] getScope();

    String getResponseType();

    String getTokenUri();
}
