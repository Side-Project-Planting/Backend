package com.example.planservice.application.dto;

import java.time.LocalDate;
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
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskUpdateServiceRequest(Long taskId, Long memberId, Long assigneeId, Long planId, String title,
                                     String description, LocalDate startDate, LocalDate endDate,
                                     List<Long> labels) {
        this.taskId = taskId;
        this.memberId = memberId;
        this.planId = planId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = labels;
    }

    public Task toEntity(Member assignee) {
        return Task.builder()
            .assignee(assignee)
            .title(title)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

}
