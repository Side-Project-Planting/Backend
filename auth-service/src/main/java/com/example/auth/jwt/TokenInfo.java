package com.example.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfo {
    private String grantType;
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
