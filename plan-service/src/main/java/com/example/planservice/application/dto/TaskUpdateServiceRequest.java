package com.example.planservice.application.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskUpdateServiceRequest {
    private Long taskId;
    private Long memberId;
    private Long planId;
    private Long managerId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskUpdateServiceRequest(Long taskId, Long memberId, Long planId, Long managerId, String name,
                                     String description, LocalDateTime startDate, LocalDateTime endDate,
                                     List<Long> labels) {
        this.taskId = taskId;
        this.memberId = memberId;
        this.planId = planId;
        this.managerId = managerId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = (labels != null) ? labels : Collections.emptyList();
    }

    public Task toEntity(Member manager) {
        return Task.builder()
            .manager(manager)
            .name(name)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

}
