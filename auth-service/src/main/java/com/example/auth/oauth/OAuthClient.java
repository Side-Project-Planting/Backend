package com.example.auth.oauth;

import com.example.auth.application.dto.response.AccessTokenResponse;
import com.example.auth.application.dto.response.OAuthUserResponse;

public interface OAuthClient {
    OAuthProperties getOAuthProperties();

    AccessTokenResponse getAccessToken(String authCode);

    // TODO 삭제
    AccessTokenResponse getAccessTokenTemp(String authCode);

    OAuthUserResponse getOAuthUserResponse(String accessToken);
}
