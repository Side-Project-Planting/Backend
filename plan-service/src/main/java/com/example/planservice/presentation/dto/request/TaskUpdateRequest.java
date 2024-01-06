package com.example.planservice.presentation.dto.request;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.example.planservice.application.dto.TaskUpdateServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskUpdateRequest {
    @NotNull
    private Long planId;

    private Long memberId;

    private Long assigneeId;

    @NotBlank
    @Schema(nullable = false, example = "변경할 이름")
    private String title;

    @Schema(nullable = true, example = "이렇게 설명을 변경할거에요")
    private String description;

    @Schema(description = "추후에 날짜까지만 입력받도록 변경될 예정", nullable = true, example = "2023-11-08T08:00:00")
    private LocalDate startDate;

    @Schema(description = "추후에 날짜까지만 입력받도록 변경될 예정", nullable = true, example = "2023-11-09T08:00:00")
    private LocalDate endDate;

    @Schema(description = "만약 Null이 입력된다면 [] 가 대신 들어감", nullable = true, example = "[1,2,3]")
    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskUpdateRequest(Long planId, Long memberId, Long assigneeId, String title, String description, LocalDate startDate,
                              LocalDate endDate, List<Long> labels) {
        this.planId = planId;
        this.memberId = memberId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = (labels != null) ? labels : Collections.emptyList();
    }

    public TaskUpdateServiceRequest toServiceRequest(@NotNull Long memberId, @NotNull Long taskId) {
        return TaskUpdateServiceRequest.builder()
            .taskId(taskId)
            .planId(planId)
            .memberId(memberId)
            .assigneeId(assigneeId)
            .title(title)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .labels(labels)
            .taskId(taskId)
            .build();
    }
}
