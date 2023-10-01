package com.example.demo.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.application.AuthService;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.presentation.dto.response.GetAuthorizedUriResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@DisplayName("AuthController 슬라이싱 테스트")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("입력받은 Provider를 지원한다면 200번 상태와 해당 Provider의 authorized uri를 반환한다")
    void getAuthorizedUri() throws Exception {
        // given
        String providerName = "google";
        GetAuthorizedUriResponse response = new GetAuthorizedUriResponse("https://answer-uri");

        // stub
        when(authService.getAuthorizedUri(providerName))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get(String.format("/oauth/%s/authorized-uri", providerName)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.authorizedUri").value("https://answer-uri"));
    }

    @Test
    @DisplayName("입력받은 Provider를 지원하지 않는다면 404번 예외를 반환한다")
    void cantGetAuthorizedUriAboutNotSupportedProvider() throws Exception {
        // given
        String providerName = "google";

        // stub
        when(authService.getAuthorizedUri(providerName))
            .thenThrow(new ApiException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));

        // when & then
        mockMvc.perform(get(String.format("/oauth/%s/authorized-uri", providerName)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage()));
    }
}