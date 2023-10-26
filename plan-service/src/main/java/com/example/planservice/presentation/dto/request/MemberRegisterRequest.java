package com.example.planservice.presentation.dto.request;

import org.hibernate.validator.constraints.URL;

import com.example.planservice.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberRegisterRequest {
    @URL
    private String profileUri;

    @NotBlank
    private String name;

    @Email
    private String email;

    private boolean receiveEmails;

    @Builder
    private MemberRegisterRequest(String profileUri, String name, String email, boolean receiveEmails) {
        this.profileUri = profileUri;
        this.name = name;
        this.email = email;
        this.receiveEmails = receiveEmails;
    }

    public Member toEntity() {
        return Member.createNormalUser()
            .profileUri(profileUri)
            .name(name)
            .email(email)
            .receiveEmails(receiveEmails)
            .build();
    }
}
