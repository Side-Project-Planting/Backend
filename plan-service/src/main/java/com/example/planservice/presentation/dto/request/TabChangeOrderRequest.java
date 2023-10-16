package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
public class TabChangeOrderRequest {
    @NotNull
    Long planId;

    @NotNull
    Long targetId;

    @NotNull
    Long newPrevId;

    @Builder
    private TabChangeOrderRequest(@NotNull Long planId, @NotNull Long targetId, @NotNull Long newPrevId) {
        this.planId = planId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
