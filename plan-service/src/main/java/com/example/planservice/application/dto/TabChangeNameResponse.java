package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeNameResponse {
    private Long id;
    private String name;

    @Builder
    public TabChangeNameResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
