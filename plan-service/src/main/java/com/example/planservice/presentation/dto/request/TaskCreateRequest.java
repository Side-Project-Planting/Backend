package com.example.planservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(nullable = true, example = "1")
    private Long assigneeId;

    @NotBlank
    @Schema(nullable = false, example = "코테보기")
    private String title;

    @Schema(nullable = true, example = "태스크 설명")
    private String description;

    @Schema(description = "추후에 날짜까지만 입력받도록 변경될 예정", nullable = true, example = "2023-11-08T08:00:00")
    private LocalDateTime startDate;

    @Schema(description = "추후에 날짜까지만 입력받도록 변경될 예정", nullable = true, example = "2023-11-09T08:00:00")
    private LocalDateTime endDate;

    @Schema(description = "만약 Null이 입력된다면 [] 가 대신 들어감", nullable = true, example = "[1,2,3]")
    private List<Long> labels;

    @Builder
    @SuppressWarnings("java:S107")
    private TaskCreateRequest(Long planId, Long tabId, Long assigneeId, String title, String description,
                              LocalDateTime startDate, LocalDateTime endDate, List<Long> labels) {
        this.planId = planId;
        this.tabId = tabId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.labels = (labels != null) ? labels : Collections.emptyList();
    }

}
