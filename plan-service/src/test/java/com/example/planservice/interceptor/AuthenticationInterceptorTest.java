package com.example.planservice.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.PathMatcher;

@SpringBootTest
class AuthenticationInterceptorTest {
    AuthenticationInterceptor interceptor;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @Autowired
    PathMatcher pathMatcher;

    @BeforeEach
    void setup() {
        interceptor = new AuthenticationInterceptor(pathMatcher);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "10", "11111111", "9223372036854775807"})
    @DisplayName("X-User-Id가 Long 타입이면 AuthenticationInterceptor의 preHandle 메서드는 true를 반환한다")
    void authenticationInterceptorSuccess(String value) {
        // given
        request.addHeader("X-User-Id", value);

        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("X-User-Id가 없으면 AuthenticationInterceptor의 preHandle 메서드는 false를 반환한다")
    void authenticationInterceptorFailUserIdNotFound() {
        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "d", "a", "9223372036854775808"})
    @DisplayName("X-User-Id가 숫자가 아니면 AuthenticationInterceptor의 preHandle 메서드는 false를 반환한다")
    void authenticationInterceptorFailUserIdNotFound(String value) {
        // given
        request.addHeader("X-User-Id", value);

        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"POST,/members"})
    @DisplayName("whitelist에 존재하는 값들은 true를 반환한다")
    void testPreHandleTrueUriInWhitelist(String method, String uri) throws Exception {
        // given
        request.setMethod(method);
        request.setRequestURI(uri);

        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"POST,/members/", "POST,/members/1"})
    @DisplayName("whitelist에 존재하지 않는 값들은 X-User-Id가 없을 때 false를 반환한다")
    void testPreHandleFalseUriNotInWhitelist(String method, String uri) throws Exception {
        // given
        request.setMethod(method);
        request.setRequestURI(uri);

        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isFalse();
    }
}
