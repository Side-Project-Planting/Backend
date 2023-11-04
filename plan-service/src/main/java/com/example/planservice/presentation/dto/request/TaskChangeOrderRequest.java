package com.example.planservice.presentation.dto.request;

import org.jetbrains.annotations.NotNull;

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
    public TaskChangeOrderRequest(@NotNull Long planId, @NotNull Long targetTabId, @NotNull Long targetId,
                                  Long newPrevId) {
        this.planId = planId;
        this.targetTabId = targetTabId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
