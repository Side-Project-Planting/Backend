package com.example.planservice.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.task.Task;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(nullable = false, example = "[1,3,2]")
    private List<Long> labels;

    @Schema(nullable = false, example = "태스크 제목")
    private String title;

    @Schema(nullable = true, example = "태스크 설명")
    private String description;

    @Schema(description = "추후에 날짜까지만 보내줄 예정", nullable = true, example = "2023-11-08T08:00:00")
    private LocalDate startDate;

    @Schema(description = "추후에 날짜까지만 보내줄 예정", nullable = true, example = "2023-11-09T08:00:00")
    private LocalDate endDate;

    private Long nextId;

    private Long prevId;

    @Builder
    @SuppressWarnings("java:S107")
    public TaskFindResponse(Long id, Long tabId, Long planId, Long managerId, List<Long> labels, String title,
                            String description, LocalDate startDate, LocalDate endDate, Long nextId,
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
            .title(task.getTitle())
            .description(task.getDescription())
            .startDate(task.getStartDate())
            .endDate(task.getEndDate())
            .nextId(nextId)
            .prevId(prevId)
            .build();
    }
}
