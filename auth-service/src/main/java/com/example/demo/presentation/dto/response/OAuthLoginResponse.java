package com.example.demo.presentation.dto.response;

import com.example.demo.domain.OAuthMember;
import com.example.demo.jwt.TokenInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private String profileUrl;
    private String email;
    private boolean isNew;

    @Builder
    private OAuthLoginResponse(String accessToken, String refreshToken, String grantType,
                               String profileUrl, String email, boolean isNew) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.profileUrl = profileUrl;
        this.email = email;
        this.isNew = isNew;
    }

    public static OAuthLoginResponse create(OAuthMember oAuthMember, TokenInfo tokenInfo) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(tokenInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(oAuthMember.getProfileUrl())
            .email(oAuthMember.getEmail())
            .isNew(oAuthMember.isNew())
            .build();
    }
}
