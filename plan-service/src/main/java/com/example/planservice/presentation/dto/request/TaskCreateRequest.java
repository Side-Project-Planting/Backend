package com.example.planservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class TaskCreateRequest {
    @NotNull
    private Long planId;

    @NotNull
    private Long tabId;

    private Long managerId;

    @NotBlank
    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskCreateRequest(Long planId, Long tabId, Long managerId, String name, String description,
                              LocalDateTime startDate, LocalDateTime endDate, List<Long> labels) {
        this.planId = planId;
        this.tabId = tabId;
        this.managerId = managerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = (labels != null) ? labels : Collections.emptyList();
    }

}
