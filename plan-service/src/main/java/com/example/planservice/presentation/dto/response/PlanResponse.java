package com.example.planservice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PlanResponse {
    private Long id;

    @Schema(nullable = false, example = "플랜명")
    private String title;

    @Schema(nullable = true, example = "플랜설명")
    private String description;

    @Schema(nullable = false, example = "[1]")
    private List<MemberOfPlanResponse> members;

    @Schema(nullable = false, example = "[1,2]")
    private List<Long> tabOrder;

    @Schema(description = "탭에 대한 정보들이 정렬되지 않고 들어 있습니다", nullable = false, example = "[]")
    private List<TabOfPlanResponse> tabs;

    @Schema(description = "태스크에 대한 정보들이 정렬되지 않고 들어 있습니다", nullable = false, example = "[]")
    private List<TaskOfPlanResponse> tasks;

    @Schema(description = "라벨에 대한 정보들이 정렬되지 않고 들어 있습니다", nullable = false, example = "[]")
    private List<LabelOfPlanResponse> labels;

    @Schema(nullable = true, example = "true")
    private boolean isPublic;

    @Builder
    @SuppressWarnings("java:S107")
    private PlanResponse(Long id, String title, String description, List<MemberOfPlanResponse> members,
                         List<Long> tabOrder,
                         List<TabOfPlanResponse> tabs, List<TaskOfPlanResponse> tasks, List<LabelOfPlanResponse> labels,
                         boolean isPublic) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.members = members;
        this.tabOrder = tabOrder;
        this.tabs = tabs;
        this.tasks = tasks;
        this.labels = labels;
        this.isPublic = isPublic;
    }

}
