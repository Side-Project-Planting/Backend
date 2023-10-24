package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelCreateRequest {
    private String name;
    private Long planId;

    @Builder
    private LabelCreateRequest(String name, Long planId) {
        this.name = name;
        this.planId = planId;
    }
}
