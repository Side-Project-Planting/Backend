package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeTitleResponse {
    private Long id;
    private String title;

    @Builder
    public TabChangeTitleResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
