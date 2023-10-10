package com.example.gatewayservice.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Jwt Properties 파일이 잘 초기화되었는지 확인하는 테스트")
class JwtPropertiesTest {
    @Autowired
    JwtProperties jwtProperties;

    @Test
    @DisplayName("Jwt 프로퍼티는 secret을 반환할 수 있다")
    void canReturnSecret() {
        assertThat(jwtProperties.getSecret()).isNotBlank();
    }
}