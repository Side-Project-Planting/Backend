package com.example.planservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.planservice.interceptor.AuthenticationInterceptor;
import com.example.planservice.interceptor.CorsInvalidateInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(pathMatcher()));
        registry.addInterceptor(new CorsInvalidateInterceptor());
    }
}
