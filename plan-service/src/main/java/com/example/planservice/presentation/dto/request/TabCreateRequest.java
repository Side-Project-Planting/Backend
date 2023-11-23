package com.example.planservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabCreateRequest {
    @NotBlank
    private String title;

    @NotNull
    private Long planId;

    @Builder
    private TabCreateRequest(String title, Long planId) {
        this.title = title;
        this.planId = planId;
    }
}
