package com.example.auth.presentation;

import static com.example.auth.presentation.CustomCookiesProperties.CookieInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomCookieManagerTest {

    CustomCookiesProperties customCookiesProperties;
    CustomCookieManager customCookieManager;

    @BeforeEach
    void setUp() {
        customCookiesProperties = new CustomCookiesProperties();
    }

    @Test
    @DisplayName("프로퍼티에 Refresh Token이 있으면 Refresh Token을 생성할 수 있다")
    void testCreateRefreshToken() throws Exception {
        // given
        CookieInfo refreshCookieInfo = createCookieInfo("refresh");
        customCookiesProperties.setCookieInfos(List.of(refreshCookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String refreshToken = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(refreshToken).startsWith("refresh=hello");
    }

    @Test
    @DisplayName("HttpOnly 쿠키를 만든다")
    void testCreateHttpOnlyCookie() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setHttpOnly(true);
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(token).startsWith("refresh=hello");
        assertThat(token).contains("HttpOnly");
    }

    @Test
    @DisplayName("Secure 쿠키를 만든다")
    void testCreateSecureCookie() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setSecure(true);
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(token).startsWith("refresh=hello");
        assertThat(token).contains("Secure");
    }

    @Test
    @DisplayName("쿠키의 MaxAge를 지정한다")
    void testCreateCookieWithMaxAgeOption() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setMaxAge(100000);
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(token).startsWith("refresh=hello");
        assertThat(token).contains("Max-Age=" + cookieInfo.getMaxAge());
    }

    @Test
    @DisplayName("쿠키의 Path를 지정한다")
    void testCreateCookieWithPathOption() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setPath("/");
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(token).startsWith("refresh=hello");
        assertThat(token).contains("Path=" + cookieInfo.getPath());
    }

    @Test
    @DisplayName("쿠키의 SameSite를 지정한다")
    void testCreateCookieWithSameSiteOption() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setSameSite("Strict");
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");

        // then
        assertThat(token).startsWith("refresh=hello");
        assertThat(token).contains("SameSite=" + cookieInfo.getSameSite());
    }

    @Test
    @DisplayName("쿠키의 각 속성은 세미 콜론으로 분리한다")
    void testCreateCookie() throws Exception {
        // given
        CookieInfo cookieInfo = createCookieInfo("refresh");
        cookieInfo.setSameSite("Strict");
        cookieInfo.setSecure(true);
        cookieInfo.setHttpOnly(true);
        customCookiesProperties.setCookieInfos(List.of(cookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when
        String token = customCookieManager.createRefreshToken("hello");
        String[] values = token.split("; ");

        // then
        assertThat(values).contains("refresh=hello", "SameSite=Strict", "Secure", "HttpOnly");
    }


    @Test
    @DisplayName("프로퍼티에 Refresh Token에 대한 정보를 입력하지 않았다면 Refresh Token을 생성할 수 없다")
    void testCreateRefreshTokenFail() throws Exception {
        // given
        CookieInfo refreshCookieInfo = createCookieInfo("not-refresh");
        customCookiesProperties.setCookieInfos(List.of(refreshCookieInfo));
        customCookieManager = new CustomCookieManager(customCookiesProperties);

        // when & then
        assertThatThrownBy(() -> customCookieManager.createRefreshToken("hello"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("해당되는 이름의 토큰은 만들 수 없습니다");
    }


    private CookieInfo createCookieInfo(String name) {
        CookieInfo cookieInfo = new CookieInfo();
        cookieInfo.setName(name);
        return cookieInfo;
    }

}