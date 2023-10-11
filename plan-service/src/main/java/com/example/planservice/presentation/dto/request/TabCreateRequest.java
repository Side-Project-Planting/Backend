package com.example.planservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabCreateRequest {
    @NotBlank
    private String name;

    @Builder
    private TabCreateRequest(String name) {
        this.name = name;
    }
}
