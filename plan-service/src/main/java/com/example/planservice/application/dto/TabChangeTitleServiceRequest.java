package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeTitleServiceRequest {
    private Long planId;
    private Long tabId;
    private Long memberId;
    private String title;

    @Builder
    private TabChangeTitleServiceRequest(Long planId, Long tabId, Long memberId, String title) {
        this.planId = planId;
        this.tabId = tabId;
        this.memberId = memberId;
        this.title = title;
    }
}
