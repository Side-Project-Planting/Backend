package com.example.planservice.presentation.dto.request;

import org.jetbrains.annotations.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeOrderRequest {
    @NotNull
    private Long planId;

    @NotNull
    private Long targetId;

    @NotNull
    private Long newPrevId;

    @Builder
    private TabChangeOrderRequest(@NotNull Long planId, @NotNull Long targetId, @NotNull Long newPrevId) {
        this.planId = planId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
