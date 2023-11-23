package com.example.planservice.presentation.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * newPrevId가 Null이 입력되면 해당 태스크를 target 탭의 첫 번째 위치로 이동시킨다
 */
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
    private TaskChangeOrderRequest(Long planId, Long targetTabId, Long targetId, Long newPrevId) {
        this.planId = planId;
        this.targetTabId = targetTabId;
        this.targetId = targetId;
        this.newPrevId = newPrevId;
    }
}
