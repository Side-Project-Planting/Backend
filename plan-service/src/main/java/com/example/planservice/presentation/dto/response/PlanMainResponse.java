package com.example.planservice.presentation.dto.response;

import java.util.List;

import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlanMainResponse {

    private String title;
    private Long planId;
    private List<Long> tabOrder;
    private List<TabInfo> tabs;

    @Getter
    public static class TabInfo {
        private Long tabId;
        private String title;
        private List<Long> taskOrder;
        private List<TaskInfo> taskList;

        @Builder
        private TabInfo(Long tabId, List<Long> taskOrder, String title, List<TaskInfo> taskList) {
            this.tabId = tabId;
            this.taskOrder = taskOrder;
            this.title = title;
            this.taskList = taskList;
        }

        public static TabInfo from(Tab tab, List<Long> taskOrder, List<TaskInfo> taskList) {
            return builder()
                .tabId(tab.getId())
                .taskOrder(taskOrder)
                .title(tab.getTitle())
                .taskList(taskList)
                .build();
        }
    }

    @Getter
    @Builder
    public static class TaskInfo {
        private Long taskId;
        private String title;
        private int dDay;

        public static TaskInfo from(Task task) {
            return builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .dDay(task.getDday())
                .build();
        }
    }

    @Builder
    private PlanMainResponse(String title, Long planId, List<Long> tabOrder, List<TabInfo> tabs) {
        this.title = title;
        this.planId = planId;
        this.tabOrder = tabOrder;
        this.tabs = tabs;
    }

    public static PlanMainResponse from(Plan plan, List<Long> tabOrder, List<TabInfo> tabs) {
        return builder()
            .title(plan.getTitle())
            .planId(plan.getId())
            .tabOrder(tabOrder)
            .tabs(tabs)
            .build();
    }

}
