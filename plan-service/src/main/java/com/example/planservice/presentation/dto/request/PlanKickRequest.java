package com.example.planservice.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanKickRequest {
    @Schema(nullable = false, example = "[1,2,3]")
    private List<Long> kickingMemberIds;

    @Builder
    private PlanKickRequest(List<Long> kickingMemberIds) {
        this.kickingMemberIds = kickingMemberIds;
    }

}
