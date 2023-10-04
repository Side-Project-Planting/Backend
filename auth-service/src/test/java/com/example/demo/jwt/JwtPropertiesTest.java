package com.example.demo.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("JWT Properties 파일이 잘 초기화되었는지 확인하는 테스트")
class JwtPropertiesTest {
    @Autowired
    JwtProperties properties;

    @Test
    @DisplayName("AccessTokenExpires는 양수이다")
    void validateAccessTokenExpires() {
        assertThat(properties.getAccessTokenExpires()).isPositive();
    }

    @Test
    @DisplayName("AccessTokenExpires는 양수이다")
    void validateRefreshTokenExpires() {
        assertThat(properties.getRefreshTokenExpires()).isPositive();
    }

    @Test
    @DisplayName("Secret 값이 존재한다")
    void validateSecret() {
        assertThat(properties.getSecret()).isNotBlank();
    }
}
