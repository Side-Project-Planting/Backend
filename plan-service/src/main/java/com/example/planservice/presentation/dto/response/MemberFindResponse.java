package com.example.planservice.presentation.dto.response;

import com.example.planservice.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberFindResponse {
    private Long id;
    private String name;
    private String email;
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
