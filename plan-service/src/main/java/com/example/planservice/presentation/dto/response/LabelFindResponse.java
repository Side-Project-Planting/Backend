package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.label.Label;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelFindResponse {
    private Long id;

    private Long planId;

    @Schema(nullable = false, example = "라벨명")
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
