package com.example.demo.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final String BEARER = "Bearer";

    private JwtProperties properties;
    private final Key key;

    @Autowired
    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateTokenInfo(Long id, LocalDateTime currentDateTime) {
        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        long now = currentDate.getTime();
        String accessToken = makeToken(String.valueOf(id), properties.getAccessTokenExpires(), now);
        String refreshToken = makeToken(String.valueOf(id), properties.getRefreshTokenExpires(), now);

        return TokenInfo.builder()
            .grantType(BEARER)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private String makeToken(String subject, Long expires, long now) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(new Date(now + expires * 1000L))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

}
