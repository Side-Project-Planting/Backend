package com.example.auth.oauth.google;

import com.example.auth.domain.OAuthType;
import com.example.auth.oauth.OAuthClient;
import com.example.auth.oauth.OAuthProperties;
import com.example.auth.oauth.OAuthProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {
    private static final OAuthType TYPE = OAuthType.GOOGLE;

    private final OAuthProperties googleProperties;

    private final OAuthClient client;

    @Override
    public OAuthType getOAuthType() {
        return TYPE;
    }

    @Override
    public OAuthProperties getOAuthProperties() {
        return googleProperties;
    }

    @Override
    public OAuthClient getOAuthClient() {
        return client;
    }
}
