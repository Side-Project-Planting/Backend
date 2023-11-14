package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthInfo;
import com.example.auth.jwt.TokenInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private boolean registered;

    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    private String grantType;

    private String profileUrl;

    private String email;

    private Long authId;

    private String authorizedToken;

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

    // TODO 프로필주소, 이메일이 필요한지? 일단은 넣어서 보내기 (없어도 되면 ProviderResponse tkrwp rksmd)
    public static OAuthLoginResponse create(OAuthInfo oAuthInfo, TokenInfo tokenInfo,
                                            OAuthUserResponse providerResponse) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(tokenInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(providerResponse.getProfileUrl())
            .email(providerResponse.getEmail())
            .registered(oAuthInfo.getMemberId() != null)
            .build();
    }

    public static OAuthLoginResponse createWithoutToken(OAuthInfo oAuthInfo, OAuthUserResponse providerResponse) {
        return OAuthLoginResponse.builder()
            .authId(oAuthInfo.getId())
            .authorizedToken(oAuthInfo.getAuthorizedToken())
            .profileUrl(providerResponse.getProfileUrl())
            .email(providerResponse.getEmail())
            .registered(oAuthInfo.getMemberId() != null)
            .build();
    }
}
