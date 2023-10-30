package com.example.auth.presentation.dto.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @URL
    private String profileUrl;
    @NotBlank
    private String name;

    @NotNull
    private Long authId;
}
