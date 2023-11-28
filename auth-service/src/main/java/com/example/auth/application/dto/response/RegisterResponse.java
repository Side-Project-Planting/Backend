package com.example.auth.application.dto.response;

import com.example.auth.jwt.TokenInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterResponse {
    @Schema(description = "회원가입된 Member의 ID", nullable = false, example = "1")
    private Long id;

    @Schema(nullable = true, example = "JWT 토큰")
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    @Schema(nullable = true, example = "Bearer ")
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
