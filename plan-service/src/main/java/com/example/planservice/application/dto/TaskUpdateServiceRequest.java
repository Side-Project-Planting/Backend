package com.example.planservice.application.dto;

import java.time.LocalDateTime;
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
    private Long assigneeId;
    private Long planId;
    private Long managerId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskUpdateServiceRequest(Long taskId, Long memberId, Long assigneeId, Long planId, Long managerId, String title,
                                     String description, LocalDateTime startDate, LocalDateTime endDate,
                                     List<Long> labels) {
        this.taskId = taskId;
        this.memberId = memberId;
        this.assigneeId = assigneeId;
        this.planId = planId;
        this.managerId = managerId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = labels;
    }

    public Task toEntity(Member manager) {
        return Task.builder()
            .manager(manager)
            .title(title)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

}
