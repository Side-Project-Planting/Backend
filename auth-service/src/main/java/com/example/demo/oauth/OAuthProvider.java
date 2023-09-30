package com.example.demo.oauth;

public interface OAuthProvider {
    boolean match(String name);

    String getAuthorizedUrlWithParams(String state);

    String getAuthorizedUrl();

    String getClientId();

    String getRedirectUri();

    String[] getScope();

    String getResponseType();}
