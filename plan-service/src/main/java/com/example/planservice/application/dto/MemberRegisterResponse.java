package com.example.planservice.application.dto;

import com.example.planservice.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberRegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String profileUri;
    private boolean receiveEmails;

    public static MemberRegisterResponse of(Member member) {
        MemberRegisterResponse response = new MemberRegisterResponse();
        response.id = member.getId();
        response.name = member.getName();
        response.email = member.getEmail();
        response.profileUri = member.getProfileUri();
        response.receiveEmails = member.isReceiveEmails();
        return response;
    }
}
