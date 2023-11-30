package com.example.planservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

@TestConfiguration
public class TestConfig {

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

}
