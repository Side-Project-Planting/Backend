package com.example.auth.client.dto;


import org.hibernate.validator.constraints.URL;

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

    // TODO 그냥 빌더 쓰는게 좋지 않겠는지 고민해보기. 이걸 String String String으로 바꿔서 쓸 필요가 굳이 있나
    public static MemberRegisterRequest create(String profileUri, String name, String email) {
        return MemberRegisterRequest.builder()
            .profileUri(profileUri)
            .name(name)
            .email(email)
            .receiveEmails(false) // TODO 추후에 변경하기
            .build();
    }
}
