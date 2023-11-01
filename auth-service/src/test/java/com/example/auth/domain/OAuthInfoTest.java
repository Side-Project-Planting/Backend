package com.example.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OAuthInfo 테스트")
class OAuthInfoTest {
    @Test
    @DisplayName("새로운 객체가 생성되면 isOld는 false 값을 갖는다")
    void makeNew() {
        // given
        OAuthInfo info = OAuthInfo.builder().build();

        // when & then
        assertThat(info.isRegistered()).isFalse();
    }

    @Test
    @DisplayName("init 메서드가 실행되면 isOld는 true가 된다")
    void init() {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .build();

        // when
        info.init(1L);

        // then
        assertThat(info.isRegistered()).isTrue();
        assertThat(info.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Refresh Token을 갱신한다")
    void changeRefreshToken() {
        OAuthInfo info = OAuthInfo.builder()
            .refreshToken("초기값")
            .build();

        info.changeRefreshToken("리프레쉬토큰");

        assertThat(info.getRefreshToken()).isEqualTo("리프레쉬토큰");
    }

    @Test
    @DisplayName("입력받은 Refresh Token이 Member의 Refresh Token과 일치한다")
    void sameRefreshToken() {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(info.isRefreshTokenMatching("초기값")).isTrue();
    }

    @ParameterizedTest
    @DisplayName("입력받은 Refresh Token이 Member의 Refresh Token과 일치하지 않는다")
    @ValueSource(strings = {"다른값", ""})
    void notSameRefreshToken(String token) {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(info.isRefreshTokenMatching(token)).isFalse();
    }

    @Test
    @DisplayName("Member의 Refresh Token를 비교할 때 null이 입력되면 false를 반환한다")
    void notSameRefreshTokenIfInputIsNull() {
        // given
        OAuthInfo info = OAuthInfo.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(info.isRefreshTokenMatching(null)).isFalse();
    }
}
