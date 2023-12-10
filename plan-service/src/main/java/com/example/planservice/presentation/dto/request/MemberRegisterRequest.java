package com.example.planservice.presentation.dto.request;

import org.hibernate.validator.constraints.URL;

import com.example.planservice.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberRegisterRequest {
    @URL
    @Schema(nullable = false, example = "https://프로필주소")
    private String profileUri;

    @NotBlank
    @Schema(nullable = false, example = "김철수")
    private String name;

    @Email
    @Schema(nullable = false, example = "kim@gmail.com")
    private String email;

    @Schema(description = "이메일 수신 여부", nullable = false, example = "true")
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
