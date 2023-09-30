package com.example.demo.oauth;

import com.example.demo.presentation.dto.response.AccessTokenResponse;
import com.example.demo.presentation.dto.response.OAuthUserResponse;

public interface OAuthClient {
    OAuthProperties getOAuthProperties();

    AccessTokenResponse getAccessToken(String authCode);

    OAuthUserResponse getOAuthUserResponse(String accessToken);
}
