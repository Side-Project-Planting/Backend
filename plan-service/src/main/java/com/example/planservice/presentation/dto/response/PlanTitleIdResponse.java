package com.example.planservice.presentation.dto.response;


import com.example.planservice.domain.plan.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlanTitleIdResponse {
    private Long id;
    @Schema(nullable = false, example = "조회된 플랜의 제목")
    private String title;

    @Builder
    private PlanTitleIdResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static PlanTitleIdResponse from(Plan plan) {
        return builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .build();
    }
}
