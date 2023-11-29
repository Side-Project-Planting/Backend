package com.example.planservice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TabChangeOrderResponse {
    @Schema(description = "탭의 정렬된 ID가 넘어온다", nullable = false, example = "[3,2,5]")
    private List<Long> sortedTabs;
}
