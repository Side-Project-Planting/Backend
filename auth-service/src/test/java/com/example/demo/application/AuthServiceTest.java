package com.example.demo.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.presentation.dto.response.GetAuthorizedUrlResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;

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

    @Test
    void test() {
        authService.login("google", "4/0AfJohXnAkUAM-9vwtscDZc0QIT6N1TzYGkL4WTO_Sqk-51rm-Y4et7CzadiPBJUuqHnUKw`");
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