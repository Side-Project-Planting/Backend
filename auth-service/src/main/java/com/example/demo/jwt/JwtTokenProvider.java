package com.example.demo.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private static final String BEARER = "Bearer";

    private JwtProperties properties;
    private final Key key;
    private final JwtParser parser;

    @Autowired
    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build();
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

    private String makeToken(String subject, long expires, long now) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(new Date(now + expires * 1000L))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public TokenInfoResponse parse(String token) {
        try {
            Jws<Claims> jws = parser.parseClaimsJws(token);
            Claims body = jws.getBody();
            long sub = Long.parseLong(body.get("sub", String.class));
            return new TokenInfoResponse(sub);
        } catch (NumberFormatException e) {
            throw new ApiException(ErrorCode.TOKEN_ID_INVALID);
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorCode.TOKEN_TIMEOVER);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            getExpirationDateFromToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Claims getAllClaimsFromToken(String token) {
        return parser.parseClaimsJws(token).getBody();
    }
}
