package com.example.planservice.interceptor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final List<WhiteListEntry> whiteList = getWhiteList();

    private final PathMatcher pathMatcher;

    private static List<WhiteListEntry> getWhiteList() {
        return List.of(
            new WhiteListEntry(HttpMethod.POST, "/members")
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean isInWhiteList = whiteList.stream()
            .anyMatch(each -> each.match(request, pathMatcher));
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
        String uriPattern;

        public boolean match(HttpServletRequest request, PathMatcher pathMatcher) {
            String requestUri = request.getRequestURI();
            String method = request.getMethod();
            return httpMethod.toString().equals(method) && pathMatcher.match(uriPattern, requestUri);
        }
    }
}
