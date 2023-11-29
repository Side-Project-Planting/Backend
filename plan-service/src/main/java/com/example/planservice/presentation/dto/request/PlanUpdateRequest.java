package com.example.planservice.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanUpdateRequest {
    @NotBlank
    @Schema(nullable = false, example = "변경할 플랜 명")
    private String title;

    @Schema(nullable = true, example = "플랜 소개 역시 변경 가능")
    private String intro;

    @NotNull
    private boolean isPublic;

    @NotNull
    private Long ownerId;

    @Builder
    private PlanUpdateRequest(String title, String intro, boolean isPublic, Long ownerId) {
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
    }
}
