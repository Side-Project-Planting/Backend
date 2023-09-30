package com.example.demo.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GooglePropertiesTest {
    GoogleProperties properties = new GoogleProperties();

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
        assertThat(scope.length).isPositive();
        for (String eachScope : scope) {
            assertThat(eachScope).isNotBlank();
        }
    }

    @Test
    @DisplayName("구글 프로퍼티는 ResponseType 값을 반환할 수 있다")
    void canReturnResponseType() {
        assertThat(properties.getResponseType()).isNotBlank();
    }
}