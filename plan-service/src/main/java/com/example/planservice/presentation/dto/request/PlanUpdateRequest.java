package com.example.planservice.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(nullable = false, example = "변경할 플랜 명")
    private String title;

    @Schema(nullable = true, example = "플랜 소개 역시 변경 가능")
    private String intro;

    @NotNull
    private boolean isPublic;

    @NotNull
    private Long ownerId;

    @Schema(nullable = false, example = "[a@naver.com, bb@gmail.com]")
    private List<@Email String> invitedEmails;

    @Schema(nullable = false, example = "[2,3]")
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
