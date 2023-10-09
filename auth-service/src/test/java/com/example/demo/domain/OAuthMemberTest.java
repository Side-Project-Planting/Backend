package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OAuthMember 테스트")
class OAuthMemberTest {
    @Test
    @DisplayName("새로운 객체가 생성되면 isOld는 false 값을 갖는다")
    void makeNew() {
        // given
        OAuthMember member = OAuthMember.builder().build();

        // when & then
        assertThat(member.isRegistered()).isFalse();
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
        assertThat(member.isRegistered()).isTrue();
    }

    @Test
    @DisplayName("Refresh Token을 갱신한다")
    void changeRefreshToken() {
        OAuthMember member = OAuthMember.builder()
            .refreshToken("초기값")
            .build();

        member.changeRefreshToken("리프레쉬토큰");

        assertThat(member.getRefreshToken()).isEqualTo("리프레쉬토큰");
    }

    @Test
    @DisplayName("입력받은 Refresh Token이 Member의 Refresh Token과 일치한다")
    void sameRefreshToken() {
        // given
        OAuthMember member = OAuthMember.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(member.isRefreshTokenMatching("초기값")).isTrue();
    }

    @ParameterizedTest
    @DisplayName("입력받은 Refresh Token이 Member의 Refresh Token과 일치하지 않는다")
    @ValueSource(strings = {"다른값", ""})
    void notSameRefreshToken(String token) {
        // given
        OAuthMember member = OAuthMember.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(member.isRefreshTokenMatching(token)).isFalse();
    }

    @Test
    @DisplayName("Member의 Refresh Token를 비교할 때 null이 입력되면 false를 반환한다")
    void notSameRefreshTokenIfInputIsNull() {
        // given
        OAuthMember member = OAuthMember.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(member.isRefreshTokenMatching(null)).isFalse();
    }
}
