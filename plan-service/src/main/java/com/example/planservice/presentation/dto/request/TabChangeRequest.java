package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
public class TabChangeRequest {
    @NotNull
    Long planId;

    @NotNull
    Long targetId;

    @NotNull
    Long newPrevId;

    @Builder
    private TabChangeRequest(@NotNull Long planId, @NotNull Long targetId, @NotNull Long newPrevId) {
        this.planId = planId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
