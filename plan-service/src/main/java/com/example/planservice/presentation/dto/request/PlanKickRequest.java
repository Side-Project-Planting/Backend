package com.example.planservice.presentation.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanKickRequest {
    private List<Long> kickingMemberIds;

    @Builder
    private PlanKickRequest(List<Long> kickingMemberIds) {
        this.kickingMemberIds = kickingMemberIds;
    }

}
