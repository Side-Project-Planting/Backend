package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.label.Label;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelFindResponse {
    private Long id;
    private Long planId;
    private String name;

    @Builder
    private LabelFindResponse(Long id, Long planId, String name) {
        this.id = id;
        this.planId = planId;
        this.name = name;
    }


    public static LabelFindResponse from(Label label) {
        return LabelFindResponse.builder()
            .id(label.getId())
            .planId(label.getPlan().getId())
            .name(label.getName())
            .build();
    }
}
