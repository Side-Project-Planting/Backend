package com.example.planservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TaskFindResponse {
    private Long id;

    private Long tabId;

    private Long planId;

    private Long managerId;

    private List<Long> labels;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long nextId;

    private Long prevId;

    @Builder
    @SuppressWarnings("java:S107")
    public TaskFindResponse(Long id, Long tabId, Long planId, Long managerId, List<Long> labels, String title,
                            String description, LocalDateTime startDate, LocalDateTime endDate, Long nextId,
                            Long prevId) {
        this.id = id;
        this.tabId = tabId;
        this.planId = planId;
        this.managerId = managerId;
        this.labels = labels;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextId = nextId;
        this.prevId = prevId;
    }

    public static TaskFindResponse from(Task task) {
        Tab tab = task.getTab();
        List<Long> labels = task.getLabelOfTasks()
            .stream()
            .map(labelOfTask -> labelOfTask.getLabel()
                .getId())
            .toList();
        Long nextId = null;
        Long prevId = null;
        if (!Objects.equals(task.getNext(), tab.getLastDummyTask())) {
            nextId = task.getNext()
                .getId();
        }
        if (!Objects.equals(task.getPrev(), tab.getFirstDummyTask())) {
            prevId = task.getPrev()
                .getId();
        }
        return TaskFindResponse.builder()
            .id(task.getId())
            .tabId(tab.getId())
            .planId(tab.getPlan()
                .getId())
            .managerId(task.getAssignee() != null ? task.getAssignee()
                .getId() : null)
            .labels(labels)
            .title(task.getName())
            .description(task.getDescription())
            .startDate(task.getStartDate())
            .endDate(task.getEndDate())
            .nextId(nextId)
            .prevId(prevId)
            .build();
    }
}
