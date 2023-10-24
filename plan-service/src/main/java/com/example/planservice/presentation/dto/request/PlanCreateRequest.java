package com.example.planservice.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlanCreateRequest {
    @NotBlank
    private String title;

    private String intro;

    @NotNull
    private boolean isPublic;

    private List<@Email String> invitedEmails;

    @Builder
    public PlanCreateRequest(String title, String intro, boolean isPublic, List<String> invitedEmails) {
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.invitedEmails = invitedEmails;
    }
}
