package com.example.planservice.interceptor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final List<WhiteListEntry> whiteList = List.of(new WhiteListEntry(HttpMethod.POST, "/members"));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean isInWhiteList = whiteList.stream()
            .anyMatch(each -> each.match(request));
        if (isInWhiteList) {
            return true;
        }

        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            request.setAttribute("userId", userId);
            return true;
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        }
    }

    @Getter
    @AllArgsConstructor
    static class WhiteListEntry {
        HttpMethod httpMethod;
        String uri;

        public boolean match(HttpServletRequest request) {
            String requestUri = request.getRequestURI();
            String method = request.getMethod();
            return httpMethod.toString().equals(method) && uri.equals(requestUri);
        }
    }
}
