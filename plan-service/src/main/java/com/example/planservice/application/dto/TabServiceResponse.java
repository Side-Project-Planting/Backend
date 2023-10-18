package com.example.planservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabServiceResponse {
    private Long id;
    private String name;

    @Builder
    public TabServiceResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
