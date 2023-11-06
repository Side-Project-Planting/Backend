package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberOfPlanResponse {
    private Long id;
    private String name;
    private String imgSrc;  // 이미지 경로를 나타내는 필드로 추정
    private boolean isAdmin;
    private String mail;

    @Builder
    private MemberOfPlanResponse(Long id, String name, String imgSrc, String mail, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.imgSrc = imgSrc;
        this.isAdmin = isAdmin;
    }

    public static MemberOfPlanResponse to(Member member, Long planAdminId) {
        return builder()
            .id(member.getId())
            .name(member.getName())
            .mail(member.getEmail())
            .imgSrc(member.getProfileUri())
            .isAdmin(member.getId()
                .equals(planAdminId))
            .build();
    }
}
