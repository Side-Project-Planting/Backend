package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeNameServiceRequest {
    private Long planId;
    private Long tabId;
    private Long memberId;
    private String name;

    @Builder
    private TabChangeNameServiceRequest(Long planId, Long tabId, Long memberId, String name) {
        this.planId = planId;
        this.tabId = tabId;
        this.memberId = memberId;
        this.name = name;
    }
}
