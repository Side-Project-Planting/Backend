package com.example.planservice.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskOfPlanResponse {
    private Long id;
    private String title;
    private List<Long> labels;
    private Long tabId;
    private Long assigneeId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    private TaskOfPlanResponse(Long id, String title, Long tabId, List<Long> labels, Long assigneeId,
                               LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.labels = labels;
        this.tabId = tabId;
        this.assigneeId = assigneeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TaskOfPlanResponse from(Task task) {
        return builder()
            .id(task.getId())
            .title(task.getTitle())
            .labels(task.getLabelOfTasks() != null ?
                task.getLabelOfTasks()
                    .stream()
                    .map(labelOfTask -> labelOfTask.getLabel()
                        .getId())
                    .toList() :
                null)
            .tabId(task.getTab()
                .getId())
            .assigneeId(task.getAssignee()
                != null ? task.getAssignee()
                .getId() : null)
            .startDate(task.getStartDate() != null ? task.getStartDate() : null)
            .endDate(task.getEndDate() != null ? task.getEndDate() : null)
            .build();
    }
}
