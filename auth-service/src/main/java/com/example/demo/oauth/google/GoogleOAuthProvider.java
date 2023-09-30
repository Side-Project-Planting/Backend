package com.example.demo.oauth.google;

import com.example.demo.domain.OAuthType;
import com.example.demo.oauth.OAuthClient;
import com.example.demo.oauth.OAuthProperties;
import com.example.demo.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {
    private static final OAuthType TYPE = OAuthType.GOOGLE;

    private final GoogleProperties googleProperties;

    private final GoogleOAuthClient client;

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