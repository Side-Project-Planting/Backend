package com.example.planservice.presentation.dto.response;


import com.example.planservice.domain.plan.Plan;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlanTitleIdResponse {
    private Long id;
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
