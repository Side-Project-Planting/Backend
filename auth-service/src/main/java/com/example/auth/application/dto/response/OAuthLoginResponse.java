package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthInfo;
import com.example.auth.jwt.TokenInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private String profileUrl;
    private String email;
    private boolean registered;

    @Builder
    private OAuthLoginResponse(String accessToken, String refreshToken, String grantType,
                               String profileUrl, String email, boolean registered) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.profileUrl = profileUrl;
        this.email = email;
        this.registered = registered;
    }

    public static OAuthLoginResponse create(OAuthInfo oAuthInfo, TokenInfo tokenInfo) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(oAuthInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(oAuthInfo.getProfileUrl())
            .email(oAuthInfo.getEmail())
            .registered(oAuthInfo.isRegistered())
            .build();
    }
}
