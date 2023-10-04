package com.example.demo.oauth;

public interface OAuthProperties {
    String getAuthorizedUriEndpoint();

    String getClientId();

    String getClientSecret();

    String getRedirectUri();

    String[] getScope();

    String getResponseType();

    String getTokenUri();

    String getUserInfoUri();
}
