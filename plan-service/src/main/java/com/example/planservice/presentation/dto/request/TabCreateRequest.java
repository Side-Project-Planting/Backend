package com.example.planservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabCreateRequest {
    @NotBlank
    @Schema(nullable = false, example = "In Progress")
    private String title;

    @NotNull
    private Long planId;

    @Builder
    private TabCreateRequest(String title, Long planId) {
        this.title = title;
        this.planId = planId;
    }
}
