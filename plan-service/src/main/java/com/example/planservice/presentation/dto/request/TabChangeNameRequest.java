package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeNameRequest {
    private Long planId;
    private Long tabId;
    private String name;

    @Builder
    private TabChangeNameRequest(Long planId, Long tabId, String name) {
        this.planId = planId;
        this.tabId = tabId;
        this.name = name;
    }
}
