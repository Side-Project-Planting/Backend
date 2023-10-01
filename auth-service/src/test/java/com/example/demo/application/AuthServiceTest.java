package com.example.demo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.application.dto.response.RegisterResponse;
import com.example.demo.domain.AuthMemberRepository;
import com.example.demo.domain.OAuthMember;
import com.example.demo.domain.OAuthType;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.oauth.google.GoogleOAuthClient;
import com.example.demo.application.dto.response.AccessTokenResponse;
import com.example.demo.application.dto.response.GetAuthorizedUriResponse;
import com.example.demo.application.dto.response.OAuthLoginResponse;
import com.example.demo.application.dto.response.OAuthUserResponse;
import com.example.demo.presentation.dto.request.RegisterRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("AuthService 통합테스트")
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Autowired
    AuthMemberRepository authMemberRepository;

    @MockBean
    GoogleOAuthClient googleOAuthClient;

    @Test
    @DisplayName("google의 AuthorizedUrl을 가져온다")
    void getGoogleAuthorizedUrl() {
        //when
        GetAuthorizedUriResponse response = authService.getAuthorizedUri("google");

        // then
        String authorizedUrl = response.getAuthorizedUri();
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
    @DisplayName("처리할 수 없는 ProviderName으로는 AuthorizedUrl을 가져올 수 없다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void cantGetAuthorizedUrlAboutNotSupportedProviderName(String providerName) {
        assertThatThrownBy(() -> authService.getAuthorizedUri(providerName))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("기존에 로그인한 적 있는 사용자가 동일한 계정으로 로그인을 할 수 있다")
    void loginSuccessIfAlreadySaved() {
        // given
        String providerName = "google";
        String authCode = "authcode";
        String accessToken = "accessToken";

        String email = "hello@naver.com";
        String profileUrl = "https://imageurl";
        String idUsingResourceServer = "1";

        OAuthUserResponse oAuthUserResponse = createOAuthUserResponse(email, profileUrl, idUsingResourceServer);
        OAuthMember member = createOAuthMember(email, profileUrl, idUsingResourceServer);
        authMemberRepository.save(member);

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenReturn(new AccessTokenResponse(accessToken));
        when(googleOAuthClient.getOAuthUserResponse(accessToken))
            .thenReturn(oAuthUserResponse);

        //when
        OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo(oAuthUserResponse.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(oAuthUserResponse.getProfileUrl());
        assertThat(response.getGrantType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("기존에 로그인한 적 없는 사용자는 로그인이 가능하다")
    void loginSuccessIfFirstVisit() {
        // given
        String providerName = "google";
        String authCode = "authcode";
        String accessToken = "accessToken";
        OAuthUserResponse oAuthUserResponse =
            createOAuthUserResponse("hello@naver.com", "https://imageurl", "1");

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenReturn(new AccessTokenResponse(accessToken));
        when(googleOAuthClient.getOAuthUserResponse(accessToken))
            .thenReturn(oAuthUserResponse);

        //when
        OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo(oAuthUserResponse.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(oAuthUserResponse.getProfileUrl());
        assertThat(response.getGrantType()).isEqualTo("Bearer");
    }


    @ParameterizedTest
    @DisplayName("로그인할 때 지원하지 않는 ProviderName에 대해 예외를 반환한다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void returnExceptionAboutNotSupportedProviderName(String providerName) {
        // given
        String authCode = "authcode";

        // when & then
        assertThatThrownBy(() -> authService.login(providerName, authCode))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("auth code를 통해 권한을 받아올 수 없으면 예외를 반환한다")
    void returnExceptionAboutInvalidAuthCode() {
        // given
        String providerName = "google";
        String authCode = "authcode";

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenThrow(new ApiException(ErrorCode.ACCESS_TOKEN_FETCH_FAIL));

        //when & then
        assertThatThrownBy(() -> authService.login(providerName, authCode))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.ACCESS_TOKEN_FETCH_FAIL.getMessage());
    }

    @Test
    @DisplayName("access token을 사용해 UserResponse를 받아오지 못하면 예외를 반환한다")
    void returnExceptionAboutInvalidAccessToken() {
        // given
        String providerName = "google";
        String authCode = "authcode";
        String accessToken = "accessToken";

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenReturn(new AccessTokenResponse(accessToken));
        when(googleOAuthClient.getOAuthUserResponse(accessToken))
            .thenThrow(new ApiException(ErrorCode.USER_INFO_FETCH_FAIL));

        //when & then
        assertThatThrownBy(() -> authService.login(providerName, authCode))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.USER_INFO_FETCH_FAIL.getMessage());
    }

    @Test
    @DisplayName("OAuthMember를 등록한다")
    void registerOAuthMember() {
        // given
        RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");
        OAuthMember member = OAuthMember.builder()
            .profileUrl("https://oldUrl")
            .build();
        authMemberRepository.save(member);

        // when
        RegisterResponse response = authService.register(request, member.getId());

        //when & then
        assertThat(response.getId()).isNotNull();
        assertThat(member.isOld()).isTrue();
        assertThat(member.getProfileUrl()).isEqualTo("https://profileUrl");
    }

    @Test
    @DisplayName("회원가입 시 userId를 사용해 OAuthMember를 조회할 수 없으면 예외를 반환한다")
    void registerFailNotFoundUser() {
        // given
        RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");

        // when & then
        assertThatThrownBy(() -> authService.register(request, 1L))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    private Map<String, String> extractParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        for (String each : paramsStr.split("&")) {
            String[] keyAndValue = each.split("=");
            params.put(keyAndValue[0], keyAndValue[1]);
        }
        return params;
    }

    private OAuthMember createOAuthMember(String email, String profileUrl, String idUsingResourceServer) {
        return OAuthMember.builder()
            .oAuthType(OAuthType.GOOGLE)
            .email(email)
            .profileUrl(profileUrl)
            .idUsingResourceServer(idUsingResourceServer)
            .build();
    }

    private OAuthUserResponse createOAuthUserResponse(String email, String profileUrl, String idUsingResourceServer) {
        return OAuthUserResponse.builder()
            .email(email)
            .profileUrl(profileUrl)
            .idUsingResourceServer(idUsingResourceServer)
            .build();
    }
}