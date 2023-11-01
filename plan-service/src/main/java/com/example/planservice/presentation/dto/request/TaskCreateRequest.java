package com.example.planservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskCreateRequest {
    private Long planId;
    private Long tabId;
    private Long managerId;
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
        this.labels = labels;
    }

    public Task toEntity() {
        return Task.builder().build();
    }
}
