package com.example.planservice.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanKickRequest {
    private List<@Email String> kickingEmails;

    @Builder
    private PlanKickRequest(List<String> kickingEmails) {
        this.kickingEmails = kickingEmails;
    }
}
