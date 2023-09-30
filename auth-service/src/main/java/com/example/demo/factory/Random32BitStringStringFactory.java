package com.example.demo.factory;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class Random32BitStringStringFactory implements RandomStringFactory {
    /**
     * 32바이트 크기의 랜덤값을 생성한다
     *
     */
    public String create() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
