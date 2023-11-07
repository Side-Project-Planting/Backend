package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.tab.Tab;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class TabFindResponse {
    private Long id;
    private String name;
    private Long nextId;

    @Builder
    private TabFindResponse(Long id, String name, Long nextId) {
        this.id = id;
        this.name = name;
        this.nextId = nextId;
    }

    public static TabFindResponse from(Tab tab) {
        return TabFindResponse.builder()
            .id(tab.getId())
            .name(tab.getName())
            .nextId(tab.getNext() == null ? null : tab.getNext().getId())
            .build();
    }
}
