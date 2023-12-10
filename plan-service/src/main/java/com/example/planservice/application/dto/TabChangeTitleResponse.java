package com.example.planservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TabChangeTitleResponse {
    private Long id;
    @Schema(nullable = false, example = "변경된 탭 제목")
    private String title;

    @Builder
    public TabChangeTitleResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
