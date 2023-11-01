package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LabelDeleteServiceRequest {
    private Long labelId;
    private Long memberId;
    private Long planId;

    @Builder
    private LabelDeleteServiceRequest(Long labelId, Long memberId, Long planId) {
        this.labelId = labelId;
        this.memberId = memberId;
        this.planId = planId;
    }
}