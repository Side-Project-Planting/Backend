package com.example.demo.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Random32BitStringStringFactoryTest {
    Random32BitStringStringFactory factory = new Random32BitStringStringFactory();

    @Test
    @DisplayName("32바이트 크기의 랜덤값을 생성한다")
    void create() {
        //when
        String randomStr = factory.create();

        // then
        byte[] decoded = Base64.getUrlDecoder().decode(randomStr);
        assertThat(decoded).hasSize(32);
    }
}
