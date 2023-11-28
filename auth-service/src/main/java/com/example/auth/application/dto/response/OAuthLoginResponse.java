package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthInfo;
import com.example.auth.jwt.TokenInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    @Schema(description = "회원가입 여부", nullable = false, example = "true")
    private boolean registered;

    @Schema(description = "엑세스 토큰, registered가 true인 경우에만 반환됩니다.", nullable = true, example = "JWT 토큰")
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    @Schema(description = "인증 방식, registered가 true인 경우에만 반환됩니다.", nullable = true, example = "Bearer ")
    private String grantType;

    @Schema(description = "프로필 주소", nullable = false, example = "https://프로필URL")
    private String profileUrl;

    @Schema(description = "인증 방식에 등록되어 있던 이메일", nullable = false, example = "kim123@google.com")
    private String email;

    @Schema(description = "registered가 false인 경우에만 반환됩니다. 이후 POST api/auth/register 요청 보낼 때 필요합니다",
        nullable = true, example = "1")
    private Long authId;

    @Schema(description = "registered가 false인 경우에만 반환됩니다. 이후 POST api/auth/register 요청 보낼 때 필요합니다",
        nullable = true, example = "긴 랜덤 문자열")
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

    // TODO 프로필주소, 이메일이 필요한지? 일단은 넣어서 보내기 (없어도 되면 ProviderResponse 없애도 됨)
    public static OAuthLoginResponse create(OAuthInfo oAuthInfo, TokenInfo tokenInfo,
                                            OAuthUserResponse providerResponse) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(tokenInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(providerResponse.getProfileUrl())
            .email(providerResponse.getEmail())
            .registered(oAuthInfo.getMember() != null)
            .build();
    }

    public static OAuthLoginResponse createWithoutToken(OAuthInfo oAuthInfo, OAuthUserResponse providerResponse) {
        return OAuthLoginResponse.builder()
            .authId(oAuthInfo.getId())
            .authorizedToken(oAuthInfo.getAuthorizedToken())
            .profileUrl(providerResponse.getProfileUrl())
            .email(providerResponse.getEmail())
            .registered(oAuthInfo.getMember() != null)
            .build();
    }
}
