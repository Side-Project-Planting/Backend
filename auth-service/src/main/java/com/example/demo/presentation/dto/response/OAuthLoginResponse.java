package com.example.demo.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String profileUrl;
    private String email;
    private boolean isNew;

    @Builder
    private OAuthLoginResponse(String accessToken, String refreshToken, String profileUrl, String email, boolean isNew) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileUrl = profileUrl;
        this.email = email;
        this.isNew = isNew;
    }
}
