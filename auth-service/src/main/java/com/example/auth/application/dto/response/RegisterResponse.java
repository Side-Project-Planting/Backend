package com.example.auth.application.dto.response;

import com.example.auth.jwt.TokenInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String grantType;

    @Builder
    private RegisterResponse(Long id, String accessToken, String refreshToken, String grantType) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
    }

    public static RegisterResponse create(TokenInfo tokenInfo, Long id) {
        return RegisterResponse.builder()
            .id(id)
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(tokenInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .build();
    }
}
