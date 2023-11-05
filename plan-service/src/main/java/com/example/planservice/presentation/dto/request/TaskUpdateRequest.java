package com.example.planservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.example.planservice.application.dto.TaskUpdateServiceRequest;
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

    private Long managerId;

    @NotBlank
    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskUpdateRequest(Long planId, Long managerId, String name, String description, LocalDateTime startDate,
                              LocalDateTime endDate, List<Long> labels) {
        this.planId = planId;
        this.managerId = managerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = (labels != null) ? labels : Collections.emptyList();
    }

    public TaskUpdateServiceRequest toServiceRequest(@NotNull Long memberId, @NotNull Long taskId) {
        return TaskUpdateServiceRequest.builder()
            .taskId(taskId)
            .memberId(memberId)
            .planId(planId)
            .managerId(managerId)
            .name(name)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .labels(labels)
            .taskId(taskId)
            .build();
    }
}
