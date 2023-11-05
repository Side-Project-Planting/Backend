package com.example.planservice.presentation.dto.response;

import java.util.List;

import com.example.planservice.domain.tab.Tab;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabOfPlanResponse {
    private Long id;
    private String title;
    private List<Long> taskOrder;

    @Builder
    private TabOfPlanResponse(Long id, String title, List<Long> taskOrder) {
        this.id = id;
        this.title = title;
        this.taskOrder = taskOrder;
    }

    public static TabOfPlanResponse toPlanResponse(Tab tab, List<Long> taskOrder) {
        return builder()
            .id(tab.getId())
            .title(tab.getName())
            .taskOrder(taskOrder)
            .build();
    }
}
