package com.example.gatewayservice.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtValidator {
    private final JwtParser parser;

    @Autowired
    public JwtValidator(JwtProperties properties) {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        Key key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build();
    }
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        try {
            getExpirationDateFromToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return parser.parseClaimsJws(token).getBody();
    }

    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}
