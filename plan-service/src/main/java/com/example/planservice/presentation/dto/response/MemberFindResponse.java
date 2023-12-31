package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberFindResponse {
    private Long id;

    @Schema(nullable = false, example = "김철수")
    private String name;

    @Schema(nullable = false, example = "chulsu@gmail.com")
    private String email;

    @Schema(nullable = false, example = "https://프로필주소")
    private String profileUri;

    @Builder
    private MemberFindResponse(Long id, String name, String email, String profileUri) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
    }

    public static MemberFindResponse from(Member member) {
        return MemberFindResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .email(member.getEmail())
            .profileUri(member.getProfileUri())
            .build();
    }
}
