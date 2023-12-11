package com.example.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.auth.application.AuthService;
import com.example.auth.application.dto.response.GetAuthorizedUriResponse;
import com.example.auth.application.dto.response.OAuthLoginResponse;
import com.example.auth.application.dto.response.RegisterResponse;
import com.example.auth.exception.ApiException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.jwt.TokenInfo;
import com.example.auth.jwt.TokenInfoResponse;
import com.example.auth.presentation.dto.request.OAuthLoginRequest;
import com.example.auth.presentation.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;

@WebMvcTest
@DisplayName("AuthController 슬라이싱 테스트")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    CustomCookieManager customCookieManager;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.when(customCookieManager.createRefreshToken(anyString())).thenReturn("refresh=hello");
    }

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
    @DisplayName("회원가입된 사용자가 로그인에 성공하면 200번 상태와 OAuthLoginResponse와 refresh 쿠키를 반환한다")
    void login() throws Exception {
        // given
        String providerName = "google";
        OAuthLoginRequest request = new OAuthLoginRequest("authcode");
        OAuthLoginResponse response = OAuthLoginResponse.builder()
            .accessToken("access")
            .refreshToken("refresh_value")
            .grantType("Bearer")
            .profileUrl("https://이미지")
            .email("email@google.com")
            .registered(true)
            .build();

        // stub
        when(authService.login(providerName, request.getAuthCode()))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post(String.format("/oauth/%s/login", providerName))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.refreshToken").doesNotExist())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.grantType").exists())
            .andExpect(jsonPath("$.profileUrl").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.registered").exists())
            .andExpect(cookie().exists("refresh"));
    }

    @Test
    @DisplayName("최초 로그인에 성공하면 200번 상태와 OAuthLoginResponse와 refresh 쿠키를 반환한다")
    void testLoginOnUserIsFirstTime() throws Exception {
        // given
        String providerName = "google";
        OAuthLoginRequest request = new OAuthLoginRequest("authcode");
        OAuthLoginResponse response = OAuthLoginResponse.builder()
            .profileUrl("https://이미지")
            .email("email@google.com")
            .authId(1L)
            .registered(false)
            .authorizedToken("인증토큰")
            .build();

        // stub
        when(authService.login(providerName, request.getAuthCode()))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post(String.format("/oauth/%s/login", providerName))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profileUrl").value(response.getProfileUrl()))
            .andExpect(jsonPath("$.email").value(response.getEmail()))
            .andExpect(jsonPath("$.authId").value(response.getAuthId()))
            .andExpect(jsonPath("$.registered").value(response.isRegistered()))
            .andExpect(jsonPath("$.authorizedToken").value(response.getAuthorizedToken()))
            .andExpect(cookie().doesNotExist("refresh"));
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
    void testRegister() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("https://profileUrl", "김태태", 1L, "인가_코드");
        RegisterResponse registerResponse = RegisterResponse.builder()
            .refreshToken("리프레쉬_토큰")
            .build();

        // stub
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(registerResponse);

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.refreshToken").doesNotExist())
            .andExpect(cookie().exists("refresh"));
    }

    @Test
    @DisplayName("회원가입 시 profile uri 양식이 잘못되면 예외를 반환한다")
    void registerFailAboutInvalidProfileUri() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("invalidUrl", "김태태", 1L, "인가_코드");

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 시 이름이 공백이면 예외를 반환한다")
    void registerFailAboutEmptyName() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("https://profileUrl", "", 1L, "인가_코드");
        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 시 존재하지 않는 authId가 입력되면 예외를 반환한다")
    void registerFailAboutNotExistUser() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("https://profileUrl", "김태태", 1L, "인가_코드");

        // stub
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new ApiException(ErrorCode.AUTH_INFO_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
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

    @Test
    @DisplayName("유효한 Refresh Token을 사용해 Access Token을 재발급받는다")
    void refreshToken() throws Exception {
        // given
        String token = makeToken();
        String createdAccessToken = makeToken();
        String createdRefreshToken = makeToken();

        // stub
        when(authService.refreshToken(anyString()))
            .thenReturn(TokenInfo.builder()
                .accessToken(createdAccessToken)
                .refreshToken(createdRefreshToken)
                .grantType("Bearer")
                .build());

        // when & then
        mockMvc.perform(post("/auth/refresh-token")
                .header("X-User-Id", 1L)
                .cookie(new Cookie("refresh", token))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value(createdAccessToken))
            .andExpect(jsonPath("$.refreshToken").doesNotExist())
            .andExpect(jsonPath("$.grantType").value("Bearer"))
            .andExpect(cookie().exists("refresh"));
    }

    @Test
    @DisplayName("만료된 Refresh Token으로는 Access Token을 재발급받을 수 없다")
    void cantRefreshTokenBecauseOfExpired() throws Exception {
        // given
        String token = makeToken();

        // stub
        when(authService.refreshToken(anyString()))
            .thenThrow(new ApiException(ErrorCode.TOKEN_TIMEOVER));

        // when & then
        mockMvc.perform(post("/auth/refresh-token")
                .cookie(new Cookie("refresh", token))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 Refresh Token으로 Access Token을 재발급받을 수 없다")
    void cantRefreshTokenBecauseOfInvalid() throws Exception {
        // given
        String token = makeToken();

        // stub
        when(authService.refreshToken(anyString()))
            .thenThrow(new ApiException(ErrorCode.TOKEN_TIMEOVER));

        // when & then
        mockMvc.perform(post("/auth/refresh-token")
                .cookie(new Cookie("refresh", token))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("refresh 요청 시 refresh 쿠키는 필수다")
    void cantRefreshTokenBecauseTokenIsNull() throws Exception {
        // when & then
        mockMvc.perform(post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    private String makeToken() {
        return UUID.randomUUID().toString();
    }

    private RegisterRequest createRegisterRequest(String url, String name, Long authId, String authToken) {
        return RegisterRequest.builder()
            .profileUrl(url)
            .name(name)
            .authId(authId)
            .authorizedToken(authToken)
            .build();
    }
}
