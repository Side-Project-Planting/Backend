package com.example.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OAuthInfo 테스트")
class OAuthInfoTest {

    @Test
    @DisplayName("init 메서드가 실행되면 isOld는 true가 된다")
    void init() {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .build();

        // when
        info.init(1L);

        // then
        assertThat(info.getMemberId()).isEqualTo(1L);
    }
}
