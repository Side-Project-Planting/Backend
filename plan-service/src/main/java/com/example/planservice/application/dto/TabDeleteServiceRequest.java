package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabDeleteServiceRequest {
    private Long tabId;
    private Long memberId;
    private Long planId;

    @Builder
    private TabDeleteServiceRequest(Long tabId, Long memberId, Long planId) {
        this.tabId = tabId;
        this.memberId = memberId;
        this.planId = planId;
    }
}