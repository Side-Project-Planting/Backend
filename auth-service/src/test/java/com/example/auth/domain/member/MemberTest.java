package com.example.auth.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {
    @Test
    @DisplayName("Refresh Token을 갱신한다")
    void changeRefreshToken() {
        // given
        Member member = Member.builder()
            .refreshToken("old")
            .build();

        // when
        member.changeRefreshToken("리프레쉬토큰");

        // then
        assertThat(member.getRefreshToken()).isEqualTo("리프레쉬토큰");
    }

    @Test
    @DisplayName("Refresh Token이 일치하는지 검사한다")
    void sameRefreshToken() {
        // given
        Member member = Member.builder()
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
        Member member = Member.builder()
            .refreshToken("초기값")
            .build();

        // when & then
        assertThat(member.isRefreshTokenMatching(token)).isFalse();
    }

    @Test
    @DisplayName("Member의 Refresh Token를 비교할 때 null이 입력되면 false를 반환한다")
    void notSameRefreshTokenIfInputIsNull() {
        // given
        Member member = Member.builder()
            .refreshToken("old")
            .build();

        // when & then
        assertThat(member.isRefreshTokenMatching(null)).isFalse();
    }
}