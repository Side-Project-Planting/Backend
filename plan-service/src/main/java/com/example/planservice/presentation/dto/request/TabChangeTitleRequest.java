package com.example.planservice.presentation.dto.request;

import com.example.planservice.application.dto.TabChangeNameServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeTitleRequest {
    private Long planId;
    private String title;

    @Builder
    private TabChangeTitleRequest(Long planId, String title) {
        this.planId = planId;
        this.title = title;
    }

    public TabChangeNameServiceRequest toServiceRequest(Long memberId, Long tabId) {
        return TabChangeNameServiceRequest.builder()
            .planId(planId)
            .title(title)
            .tabId(tabId)
            .memberId(memberId)
            .build();
    }
}
