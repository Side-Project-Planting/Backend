package com.example.planservice.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanUpdateRequest {
    @NotBlank
    private String title;

    private String intro;

    @NotNull
    private boolean isPublic;

    @NotNull
    private Long ownerId;

    private List<@Email String> invitedEmails;
    private List<Long> kickingMemberIds;

    @Builder
    private PlanUpdateRequest(String title, String intro, boolean isPublic, Long ownerId, List<String> invitedEmails,
                              List<Long> kickingMemberIds) {
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
        this.invitedEmails = invitedEmails;
        this.kickingMemberIds = kickingMemberIds;

    }
}
