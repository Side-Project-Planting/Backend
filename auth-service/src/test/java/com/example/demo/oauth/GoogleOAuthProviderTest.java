package com.example.demo.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("GoogleOAuthProvider 테스트")
class GoogleOAuthProviderTest {
    GoogleOAuthProvider provider = new GoogleOAuthProvider();

    @Test
    @DisplayName("google이라는 값에 대해 match 메서드는 true를 반환한다")
    void matchAboutValueGoogle() {
        //given
        String value = "google";

        //when & then
        assertThat(provider.match(value)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("google 이외의 값에 대해 match 메서드는 false를 반환한다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void matchFail(String value) {

        //when & then
        assertThat(provider.match(value)).isFalse();
    }

    @Test
    @DisplayName("null 값에 대해 match 메서드는 false를 반환한다")
    void matchFailAboutNull() {
        // given
        String value = null;

        // when & then
        assertThat(provider.match(value)).isFalse();
    }

}