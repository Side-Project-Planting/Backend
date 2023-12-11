package com.example.planservice.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisUtilTest {
    @Autowired
    private RedisUtils redisUtils;

    @BeforeEach
    void setup() {
        redisUtils.setData("key", "value");
    }

    @AfterEach
    void tearDown() {
        redisUtils.deleteData("key");
    }

    @Test
    @DisplayName("Redis에 데이터를 저장하고 가져온다")
    void saveAndGetData() {
        // when
        String result = redisUtils.getData("key");

        // then
        assertThat(result).isEqualTo("value");
    }

    @Test
    @DisplayName("Redis에 데이터를 저장하고 삭제한다")
    void saveAndDeleteData() {
        // when
        redisUtils.deleteData("key");
        String result = redisUtils.getData("key");

        // then
        assertThat(result).isNull();
    }

}


