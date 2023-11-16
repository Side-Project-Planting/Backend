package com.example.planservice.presentation.dto.request;

import com.example.planservice.application.dto.TabChangeTitleServiceRequest;
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

    public TabChangeTitleServiceRequest toServiceRequest(Long memberId, Long tabId) {
        return TabChangeTitleServiceRequest.builder()
            .planId(planId)
            .title(title)
            .tabId(tabId)
            .memberId(memberId)
            .build();
    }
}
