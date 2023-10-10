package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthMember;
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

    public static OAuthLoginResponse create(OAuthMember oAuthMember, TokenInfo tokenInfo) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(oAuthMember.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(oAuthMember.getProfileUrl())
            .email(oAuthMember.getEmail())
            .registered(oAuthMember.isRegistered())
            .build();
    }
}