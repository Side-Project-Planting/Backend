package com.example.planservice.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabRetrieveResponse {
    private Long tabId;
    private Long planId;
    private String name;

    @Builder
    private TabRetrieveResponse(Long tabId, Long planId, String name) {
        this.tabId = tabId;
        this.planId = planId;
        this.name = name;
    }
}
