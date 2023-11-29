package com.example.planservice.presentation.dto.request;

import java.util.List;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.plan.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(nullable = false, example = "플랜입니다")
    private String title;

    @Schema(nullable = true, example = "플랜 간단 소개에요")
    private String intro;

    @NotNull
    @Schema(description = "외부 사용자들에게 플랜 공개 여부", nullable = false, example = "true")
    private Boolean isPublic;

    @Schema(nullable = false, example = "[kim@gmail.com, aa@naver.com]")
    private List<@Email String> invitedEmails;

    @Builder
    private PlanCreateRequest(String title, String intro, boolean isPublic, List<String> invitedEmails) {
        this.title = title;
        this.intro = intro;
        this.isPublic = isPublic;
        this.invitedEmails = invitedEmails;
    }

    public Plan toEntity(Member member) {
        return Plan.builder()
            .title(title)
            .intro(intro)
            .isPublic(isPublic)
            .owner(member)
            .build();
    }
}
