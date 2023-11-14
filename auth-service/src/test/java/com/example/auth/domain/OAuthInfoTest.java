package com.example.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.auth.domain.member.Member;

@DisplayName("OAuthInfo 테스트")
class OAuthInfoTest {

    @Test
    @DisplayName("init 메서드가 실행되면 isOld는 true가 된다")
    void init() {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .build();
        Member member = Member.builder().build();
        // when
        info.init(member);

        // then
        assertThat(info.getMember()).isNotNull();
    }
}
