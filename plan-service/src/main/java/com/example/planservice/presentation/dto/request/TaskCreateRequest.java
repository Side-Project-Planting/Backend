package com.example.planservice.presentation.dto.request;

import java.time.LocalDateTime;

import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskCreateRequest {
    private Long tabId;
    private Long managerId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    private TaskCreateRequest(Long tabId, Long managerId, String name, String description,
                              LocalDateTime startDate, LocalDateTime endDate) {
        this.tabId = tabId;
        this.managerId = managerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Task toEntity() {
        return Task.builder().build();
    }
}
