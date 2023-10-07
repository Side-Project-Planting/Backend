package com.example.gatewayservice.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
class JwtValidatorTest {
    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    JwtProperties properties;

    Key key;

    @BeforeEach
    void setup() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("토큰이 기한이 만료되지 않으면 true를 반환한다")
    void validateToken() {
        // given
        LocalDateTime expired = LocalDateTime.of(3099, 1, 1, 0, 0);
        String token = makeToken("1L", expired);

        // when & then
        assertThat(jwtValidator.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰이 기한이 만료되었으면 false를 반환한다")
    void validateExpiredToken() {
        // given
        LocalDateTime expired = LocalDateTime.of(1999, 1, 1, 0, 0);
        String token = makeToken("1L", expired);

        // when & then
        assertThat(jwtValidator.validateToken(token)).isFalse();
    }

    private String makeToken(String subject, LocalDateTime expiredDateTime) {
        Date expiredDate = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expiredDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

}