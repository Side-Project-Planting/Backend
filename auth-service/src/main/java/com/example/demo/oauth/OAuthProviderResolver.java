package com.example.demo.oauth;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class OAuthProviderResolver {
    private final List<OAuthProvider> oAuthProviders;

    public OAuthProvider find(String providerName) {
        return oAuthProviders.stream()
            .filter(provider -> provider.match(providerName))
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));
    }
}
