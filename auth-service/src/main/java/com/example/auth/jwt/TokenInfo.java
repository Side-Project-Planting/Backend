package com.example.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfo {
    @Schema(nullable = true, example = "Bearer ")
    private String grantType;

    @Schema(nullable = true, example = "JWT 토큰")
    private String accessToken;
    @JsonIgnore
    private String refreshToken;

    @Builder
    private TokenInfo(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
