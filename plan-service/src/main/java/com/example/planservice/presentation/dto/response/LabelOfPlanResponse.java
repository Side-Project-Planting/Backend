package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.label.Label;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelOfPlanResponse {
    private Long id;
    private String value;

    @Builder
    private LabelOfPlanResponse(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public static LabelOfPlanResponse toPlanResponse(Label label) {
        return builder()
            .id(label.getId())
            .value(label.getName())
            .build();
    }
}
