package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuthMember 테스트")
class OAuthMemberTest {
    @Test
    @DisplayName("새로운 객체가 생성되면 isOld는 false 값을 갖는다")
    void makeNew() {
        // given
        OAuthMember member = OAuthMember.builder().build();

        // when & then
        assertThat(member.isOld()).isFalse();
    }

    @Test
    @DisplayName("init 메서드가 실행되면 isOld는 true가 되고 profileUrl을 변경한다")
    void init() {
        // given
        OAuthMember member = OAuthMember.builder()
            .profileUrl("https://old")
            .build();

        // when
        member.init("https://new");

        // then
        assertThat(member.getProfileUrl()).isEqualTo("https://new");
        assertThat(member.isOld()).isTrue();

    }
}