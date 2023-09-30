package com.example.demo.oauth;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {
    private static final String NAME = "google";

    private final GoogleProperties googleProperties;

    @Override
    public boolean match(String name) {
        return Objects.equals(NAME, name);
    }

    @Override
    public String getAuthorizedUriEndpoint() {
        return googleProperties.getAuthorizedUriEndpoint();
    }

    @Override
    public String getClientId() {
        return googleProperties.getClientId();
    }

    @Override
    public String getRedirectUri() {
        return googleProperties.getRedirectUri();
    }

    @Override
    public String[] getScope() {
        return googleProperties.getScope();
    }

    @Override
    public String getResponseType() {
        return googleProperties.getResponseType();
    }

    @Override
    public String getTokenUri() {
        return googleProperties.getTokenUri();
    }

    @Override
    public String getClientSecret() {
        // TODO 추후 삭제하기
        return googleProperties.getClientSecret();
    }

}
