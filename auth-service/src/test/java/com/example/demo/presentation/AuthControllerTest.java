package com.example.demo.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.application.AuthService;
import com.example.demo.application.dto.response.GetAuthorizedUriResponse;
import com.example.demo.application.dto.response.OAuthLoginResponse;
import com.example.demo.application.dto.response.RegisterResponse;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.jwt.TokenInfoResponse;
import com.example.demo.presentation.dto.request.OAuthLoginRequest;
import com.example.demo.presentation.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@DisplayName("AuthController 슬라이싱 테스트")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

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
            .andExpect(jsonPath("$.authorizedUri").value("https://answer-uri"));
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

    @Test
    @DisplayName("로그인에 성공하면 200번 상태와 OAuthLoginResponse를 반환한다")
    void login() throws Exception {
        // given
        String providerName = "google";
        OAuthLoginRequest request = new OAuthLoginRequest("authcode");
        OAuthLoginResponse response = OAuthLoginResponse.builder()
            .accessToken("access")
            .refreshToken("refresh")
            .grantType("Bearer")
            .profileUrl("https://이미지")
            .email("email@google.com")
            .old(true)
            .build();

        // stub
        when(authService.login(providerName, request.getAuthCode()))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post(String.format("/oauth/%s/login", providerName))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.grantType").exists())
            .andExpect(jsonPath("$.profileUrl").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.old").exists());
    }

    @Test
    @DisplayName("액세스 토큰을 받아오는 데 실패하면 로그인이 불가능하다")
    void loginFailAboutAccessTokenFetchFail() throws Exception {
        // given
        String providerName = "google";
        String authCode = "authcode";

        // stub
        when(authService.login(providerName, authCode))
            .thenThrow(new ApiException(ErrorCode.ACCESS_TOKEN_FETCH_FAIL));

        // when & then
        mockMvc.perform(post(String.format("/oauth/%s/login", providerName))
                .content(authCode)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 정보를 받아오는 데 실패하면 로그인이 불가능하다")
    void loginFailAboutUserInfoFetchFail() throws Exception {
        // given
        String providerName = "google";
        String authCode = "authcode";

        // stub
        when(authService.login(providerName, authCode))
            .thenThrow(new ApiException(ErrorCode.USER_INFO_FETCH_FAIL));

        // when & then
        mockMvc.perform(post(String.format("/oauth/%s/login", providerName))
                .content(authCode)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원을 등록한다")
    void register() throws Exception {
        // given
        Long userId = 1L;
        RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");
        RegisterResponse registerResponse = new RegisterResponse(userId);

        // stub
        when(authService.register(request, userId))
            .thenReturn(registerResponse);

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .header("X-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 시 profile uri 양식이 잘못되면 예외를 반환한다")
    void registerFailAboutInvalidProfileUri() throws Exception {
        // given
        Long userId = 1L;
        RegisterRequest request = new RegisterRequest("invalid", "김태태");

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .header("X-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 시 이름이 공백이면 예외를 반환한다")
    void registerFailAboutEmptyName() throws Exception {
        // given
        Long userId = 1L;
        RegisterRequest request = new RegisterRequest("https://profileUrl", "");

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .header("X-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 시 존재하지 않는 userId가 입력되면 예외를 반환한다")
    void registerFailAboutNotExistUser() throws Exception {
        // given
        Long userId = 1L;
        RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");

        // stub
        when(authService.register(any(RegisterRequest.class), anyLong()))
            .thenThrow(new ApiException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .header("X-UserId", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("토큰을 파싱한다")
    void parseToken() throws Exception {
        // given
        Long userId = 1L;
        String token = "token";

        // stub
        when(authService.parse(token))
            .thenReturn(new TokenInfoResponse(userId));

        // when & then
        mockMvc.perform(get("/auth/parse")
                .queryParam("token", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @DisplayName("Token ID가 유효하지 않으면 토큰 파싱에 실패한다")
    void parseTokenBecauseOfInvalidTokenId() throws Exception {
        // given
        String token = "token";

        // stub
        when(authService.parse(token))
            .thenThrow(new ApiException(ErrorCode.TOKEN_ID_INVALID));

        // when & then
        mockMvc.perform(get("/auth/parse")
                .queryParam("token", token))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Token의 기간이 만료되었으면 토큰 파싱에 실패한다")
    void parseTokenBecauseOfDurationOver() throws Exception {
        // given
        String token = "token";

        // stub
        when(authService.parse(token))
            .thenThrow(new ApiException(ErrorCode.TOKEN_TIMEOVER));

        // when & then
        mockMvc.perform(get("/auth/parse")
                .queryParam("token", token))
            .andExpect(status().isBadRequest());
    }
}