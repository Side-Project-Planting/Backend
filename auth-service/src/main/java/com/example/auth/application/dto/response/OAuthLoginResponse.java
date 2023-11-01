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
    private Long authId;
    private String authorizedToken;
    private boolean registered;

    @Builder
    @SuppressWarnings("java:S107")
    private OAuthLoginResponse(String accessToken, String refreshToken, String grantType, String profileUrl,
                               String email, Long authId, boolean registered, String authorizedToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.profileUrl = profileUrl;
        this.email = email;
        this.authId = authId;
        this.registered = registered;
        this.authorizedToken = authorizedToken;
    }

    public static OAuthLoginResponse create(OAuthInfo oAuthInfo, TokenInfo tokenInfo, String profileUrl) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(oAuthInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(profileUrl)
            .email(oAuthInfo.getEmail())
            .registered(oAuthInfo.isRegistered())
            .build();
    }

    public static OAuthLoginResponse createWithoutToken(OAuthInfo oAuthInfo, String profileUrl) {
        return OAuthLoginResponse.builder()
            .profileUrl(profileUrl)
            .email(oAuthInfo.getEmail())
            .authId(oAuthInfo.getId())
            .registered(oAuthInfo.isRegistered())
            .authorizedToken(oAuthInfo.getAuthorizedToken())
            .build();
    }
}
