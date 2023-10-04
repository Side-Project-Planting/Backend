package com.example.demo.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.oauth.google.GoogleOAuthProvider;
import com.example.demo.oauth.google.GoogleProperties;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleOAuthProvider 단위테스트")
class GoogleOAuthProviderTest {
    @Mock
    GoogleProperties properties;

    @InjectMocks
    GoogleOAuthProvider provider;

    @Test
    @DisplayName("google이라는 값에 대해 match 메서드는 true를 반환한다")
    void matchAboutValueGoogle() {
        //given
        String value = "google";

        //when & then
        assertThat(provider.match(value)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("google 이외의 값에 대해 match 메서드는 false를 반환한다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void matchFail(String value) {

        //when & then
        assertThat(provider.match(value)).isFalse();
    }

    @Test
    @DisplayName("null 값에 대해 match 메서드는 false를 반환한다")
    void matchFailAboutNull() {
        // given
        String value = null;

        // when & then
        assertThat(provider.match(value)).isFalse();
    }

    @Test
    @DisplayName("만들어진 Authentication Url은 파라미터에 clientId, redirect_uri, scope, response_type, state를 포함한다")
    void makeAuthenticationUrl() {
        // given
        setGoogleProperties();
        String state = "랜덤값";

        // when
        String authorizedUrl = provider.getAuthorizedUriWithParams(state);

        // then
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
            .containsEntry("state", state);
    }

    private Map<String, String> extractParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        for (String each : paramsStr.split("&")) {
            String[] keyAndValue = each.split("=");
            params.put(keyAndValue[0], keyAndValue[1]);
        }
        return params;
    }

    private void setGoogleProperties() {
        Mockito.when(properties.getAuthorizedUriEndpoint()).thenReturn("1");
        Mockito.when(properties.getClientId()).thenReturn("1");
        Mockito.when(properties.getRedirectUri()).thenReturn("1");
        Mockito.when(properties.getScope()).thenReturn(new String[] {"1"});
        Mockito.when(properties.getResponseType()).thenReturn("1");
    }
}
