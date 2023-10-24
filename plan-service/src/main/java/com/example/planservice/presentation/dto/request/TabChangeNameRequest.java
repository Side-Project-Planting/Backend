package com.example.planservice.presentation.dto.request;

import com.example.planservice.application.dto.TabChangeNameServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeNameRequest {
    private Long planId;
    private String name;

    @Builder
    private TabChangeNameRequest(Long planId, String name) {
        this.planId = planId;
        this.name = name;
    }

    public TabChangeNameServiceRequest toServiceRequest(Long memberId, Long tabId) {
        return TabChangeNameServiceRequest.builder()
            .planId(planId)
            .name(name)
            .tabId(tabId)
            .memberId(memberId)
            .build();
    }
}
