package com.example.auth.presentation.dto.request;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {
    @URL
    @Schema(nullable = false, example = "https://프로필주소")
    private String profileUrl;

    @NotBlank
    @Schema(nullable = false, example = "사용할 이름")
    private String name;

    @NotNull
    private Long authId;

    @NotBlank
    @Schema(nullable = false, example = "앞서 login 과정에서 받아온 authorizedToken을 입력하면 됩니다(추후 UUID로 변경될 예정)")
    private String authorizedToken;

    @Builder
    private RegisterRequest(String profileUrl, String name, Long authId, String authorizedToken) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.authId = authId;
        this.authorizedToken = authorizedToken;
    }
}
