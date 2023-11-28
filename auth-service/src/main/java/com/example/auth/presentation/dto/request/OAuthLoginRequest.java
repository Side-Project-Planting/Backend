package com.example.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequest {
    @NotBlank
    @Schema(nullable = false, example = "OAuth 통신 과정에서 받아온 Authorization Code를 입력하면 됩니다.")
    private String authCode;
}
