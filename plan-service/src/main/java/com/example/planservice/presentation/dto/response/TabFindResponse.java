package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.tab.Tab;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class TabFindResponse {
    private Long id;
    private String title;
    private Long nextId;

    @Builder
    private TabFindResponse(Long id, String title, Long nextId) {
        this.id = id;
        this.title = title;
        this.nextId = nextId;
    }

    public static TabFindResponse from(Tab tab) {
        return TabFindResponse.builder()
            .id(tab.getId())
            .title(tab.getTitle())
            .nextId(tab.getNext() == null ? null : tab.getNext().getId())
            .build();
    }
}
