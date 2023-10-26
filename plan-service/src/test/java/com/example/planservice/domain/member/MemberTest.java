package com.example.planservice.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    @DisplayName("Normal 권한을 갖는 사용자를 생성한다")
    void createNormalUser() {
        // given
        String profileImgUri = "https://da12eds.com";
        String name = "김태훈";
        String email = "a@naver.com";
        boolean receiveEmail = true;

        // when
        Member member = Member.createNormalUser()
            .profileUri(profileImgUri)
            .name(name)
            .email(email)
            .receiveEmails(receiveEmail)
            .build();

        // then
        assertThat(member.getId()).isNull();
        assertThat(member.isDeleted()).isFalse();
        assertThat(member.getProfileUri()).isEqualTo(profileImgUri);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.isReceiveEmails()).isEqualTo(receiveEmail);
    }

}