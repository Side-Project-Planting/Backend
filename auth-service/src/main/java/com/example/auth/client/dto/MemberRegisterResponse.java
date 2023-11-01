package com.example.auth.client.dto;

import lombok.Builder;
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

    @Builder
    private MemberRegisterResponse(Long id, String name, String email, String profileUri, boolean receiveEmails) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
        this.receiveEmails = receiveEmails;
    }

}
