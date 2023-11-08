package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanUpdateRequest {
    private String title;
    private String intro;
    private boolean isPublic;
    private Long ownerId;

    @Builder
    private PlanUpdateRequest(String title, String intro, boolean isPublic, Long ownerId) {
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
    }
}
