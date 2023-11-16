package com.example.planservice.presentation.dto.request;

import com.example.planservice.application.dto.TabChangeTitleServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeTitleRequest {
    @NotBlank
    private String title;

    @NotNull
    private Long planId;

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
