package com.example.planservice.presentation.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskChangeOrderRequest {
    @NotNull
    private Long planId;

    @NotNull
    private Long targetTabId;

    @NotNull
    private Long targetId;

    private Long newPrevId;

    @Builder
    public TaskChangeOrderRequest(Long planId, Long targetTabId, Long targetId, Long newPrevId) {
        this.planId = planId;
        this.targetTabId = targetTabId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
