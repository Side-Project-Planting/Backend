package com.example.planservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabCreateRequest {
    private String name;

    @Builder
    private TabCreateRequest(String name) {
        this.name = name;
    }
}
