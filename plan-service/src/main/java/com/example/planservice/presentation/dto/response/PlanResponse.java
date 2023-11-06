package com.example.planservice.presentation.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PlanResponse {
    private String title;
    private String description;
    private List<MemberOfPlanResponse> members;
    private List<Long> tabOrder;
    private List<TabOfPlanResponse> tabs;
    private List<TaskOfPlanResponse> tasks;
    private List<LabelOfPlanResponse> labels;
    private boolean isPublic;

    @Builder
    private PlanResponse(String title, String description, List<MemberOfPlanResponse> members, List<Long> tabOrder,
                         List<TabOfPlanResponse> tabs, List<TaskOfPlanResponse> tasks, List<LabelOfPlanResponse> labels,
                         boolean isPublic) {
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
