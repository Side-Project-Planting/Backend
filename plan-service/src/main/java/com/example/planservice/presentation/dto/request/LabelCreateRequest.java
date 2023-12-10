package com.example.planservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelCreateRequest {

    @NotBlank
    @Schema(nullable = false, example = "공부")
    private String name;

    @NotNull
    private Long planId;

    @Builder
    private LabelCreateRequest(String name, Long planId) {
        this.name = name;
        this.planId = planId;
    }
}
