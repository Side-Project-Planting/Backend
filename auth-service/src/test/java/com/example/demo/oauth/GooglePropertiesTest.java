package com.example.demo.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.oauth.google.GoogleProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Google Properties 파일이 잘 초기화되었는지 확인하는 테스트")
class GooglePropertiesTest {
    @Autowired
    GoogleProperties properties;

    @Test
    @DisplayName("구글 프로퍼티는 AuthorizedUriEndpoint 값을 반환할 수 있다")
    void canReturnAuthorizedUriEndpoint() {
        assertThat(properties.getAuthorizedUriEndpoint()).isNotBlank();
    }

    @Test
    @DisplayName("구글 프로퍼티는 RedirectUri 값을 반환할 수 있다")
    void canReturnRedirectUri() {
        assertThat(properties.getRedirectUri()).isNotBlank();
    }

    @Test
    @DisplayName("구글 프로퍼티는 1개 이상의 Scope 값을 반환한다")
    void canReturnScope() {
        String[] scope = properties.getScope();
        assertThat(scope).isNotEmpty();
        for (String eachScope : scope) {
            assertThat(eachScope).isNotBlank();
        }
    }

    @Test
    @DisplayName("구글 프로퍼티는 ResponseType 값을 반환할 수 있다")
    void canReturnResponseType() {
        assertThat(properties.getResponseType()).isNotBlank();
    }

    @Test
    @DisplayName("구글 프로퍼티는 tokenUri 값을 반환할 수 있다")
    void canReturnTokenUri() {
        assertThat(properties.getTokenUri()).isNotBlank();
    }
}