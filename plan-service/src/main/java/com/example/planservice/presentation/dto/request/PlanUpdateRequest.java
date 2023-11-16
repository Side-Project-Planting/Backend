package com.example.planservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanUpdateRequest {
    @NotBlank
    private String title;

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
