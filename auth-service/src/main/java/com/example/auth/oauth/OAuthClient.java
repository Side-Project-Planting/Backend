package com.example.auth.oauth;

import com.example.auth.application.dto.response.AccessTokenResponse;
import com.example.auth.application.dto.response.OAuthUserResponse;

public interface OAuthClient {
    OAuthProperties getOAuthProperties();

    AccessTokenResponse getAccessToken(String authCode);

    OAuthUserResponse getOAuthUserResponse(String accessToken);
}
