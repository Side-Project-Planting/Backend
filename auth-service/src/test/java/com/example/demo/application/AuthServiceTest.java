package com.example.demo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.oauth.google.GoogleOAuthClient;
import com.example.demo.presentation.dto.response.AccessTokenResponse;
import com.example.demo.presentation.dto.response.GetAuthorizedUrlResponse;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.hibernate.annotations.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @MockBean
    GoogleOAuthClient googleOAuthClient;

    @Test
    @DisplayName("google의 Authorized URL을 가져온다")
    void getGoogleAuthorizedUrl() {
        //when
        GetAuthorizedUrlResponse response = authService.getAuthorizedUri("google");

        // then
        String authorizedUrl = response.getAuthorizedUrl();
        String[] url = authorizedUrl.split("[?]");
        String endpoint = url[0];
        Map<String, String> params = extractParams(url[1]);

        assertThat(endpoint).isNotBlank();
        assertThat(params)
            .hasSize(5)
            .containsKey("client_id")
            .containsKey("redirect_uri")
            .containsKey("scope")
            .containsKey("response_type")
            .containsKey("state");
    }

    @ParameterizedTest
    @DisplayName("google 이외의 providerName은 지원하지 않는다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void notSupportProviderWithoutGoogle(String providerName) {
        Assertions.assertThatThrownBy(() -> authService.getAuthorizedUri(providerName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("매치되지 않는 타입입니다");
    }

    private static Map<String, String> extractParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        for (String each : paramsStr.split("&")) {
            String[] keyAndValue = each.split("=");
            params.put(keyAndValue[0], keyAndValue[1]);
        }
        return params;
    }
}