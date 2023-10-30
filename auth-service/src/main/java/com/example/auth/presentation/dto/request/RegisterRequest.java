package com.example.auth.presentation.dto.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {
    @URL
    private String profileUrl;
    @NotBlank
    private String name;

    @NotNull
    private Long authId;

    @NotBlank
    private String authorizedToken;

    @Builder
    private RegisterRequest(String profileUrl, String name, Long authId, String authorizedToken) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.authId = authId;
        this.authorizedToken = authorizedToken;
    }
}
