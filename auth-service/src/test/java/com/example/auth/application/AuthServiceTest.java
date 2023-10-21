package com.example.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.auth.application.dto.response.AccessTokenResponse;
import com.example.auth.application.dto.response.GetAuthorizedUriResponse;
import com.example.auth.application.dto.response.OAuthLoginResponse;
import com.example.auth.application.dto.response.OAuthUserResponse;
import com.example.auth.application.dto.response.RegisterResponse;
import com.example.auth.domain.AuthMemberRepository;
import com.example.auth.domain.OAuthMember;
import com.example.auth.domain.OAuthType;
import com.example.auth.exception.ApiException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.factory.RandomStringFactory;
import com.example.auth.jwt.JwtTokenProvider;
import com.example.auth.jwt.TokenInfo;
import com.example.auth.jwt.TokenInfoResponse;
import com.example.auth.oauth.OAuthProperties;
import com.example.auth.oauth.OAuthProvider;
import com.example.auth.oauth.OAuthProviderResolver;
import com.example.auth.oauth.google.GoogleOAuthClient;
import com.example.auth.oauth.google.GoogleOAuthProvider;
import com.example.auth.presentation.dto.request.RegisterRequest;
import com.example.auth.presentation.dto.request.TokenRefreshRequest;

@SpringBootTest
@Transactional
@DisplayName("AuthService 통합테스트")
class AuthServiceTest {
    AuthService authService;

    @Autowired
    AuthMemberRepository authMemberRepository;

    @Autowired
    RandomStringFactory factory;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    OAuthProviderResolver resolver;

    OAuthProvider provider;

    @Autowired
    OAuthProperties oAuthProperties;

    @MockBean
    GoogleOAuthClient googleOAuthClient;

    @BeforeEach
    void setUp() {
        provider = new GoogleOAuthProvider(oAuthProperties, googleOAuthClient);
        resolver = new OAuthProviderResolver(List.of(provider));
        authService = new AuthService(resolver, authMemberRepository, factory, jwtTokenProvider);
    }

    @Test
    @DisplayName("google의 AuthorizedUrl을 가져온다")
    void getGoogleAuthorizedUrl() {
        //when
        final GetAuthorizedUriResponse response = authService.getAuthorizedUri("google");

        // then
        final String authorizedUrl = response.getAuthorizedUri();
        final String[] url = authorizedUrl.split("[?]");
        final String endpoint = url[0];
        final Map<String, String> params = extractParams(url[1]);

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
    @ValueSource(strings = { "naver", "kakao", "Google", " ", "" })
    void cantGetAuthorizedUrlAboutNotSupportedProviderName(String providerName) {
        assertThatThrownBy(() -> authService.getAuthorizedUri(providerName))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("기존에 로그인한 적 있는 사용자가 동일한 계정으로 로그인을 할 수 있다")
    void loginSuccessIfAlreadySaved() {
        // given
        final String providerName = "google";
        final String authCode = "authcode";
        final String accessToken = "accessToken";

        final String email = "hello@naver.com";
        final String profileUrl = "https://imageurl";
        final String idUsingResourceServer = "1";

        final OAuthUserResponse oAuthUserResponse = createOAuthUserResponse(email, profileUrl, idUsingResourceServer);
        final OAuthMember member = createOAuthMember(email, profileUrl, idUsingResourceServer);
        authMemberRepository.save(member);

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
                .thenReturn(new AccessTokenResponse(accessToken));
        when(googleOAuthClient.getOAuthUserResponse(accessToken))
                .thenReturn(oAuthUserResponse);

        //when
        final OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo(oAuthUserResponse.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(oAuthUserResponse.getProfileUrl());
        assertThat(response.getGrantType()).isEqualTo("Bearer");

        assertThat(member.getRefreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("기존에 로그인한 적 없는 사용자는 로그인이 가능하다")
    void loginSuccessIfFirstVisit() {
        // given
        final String providerName = "google";
        final String authCode = "authcode";
        final String accessToken = "accessToken";
        final OAuthUserResponse oAuthUserResponse =
                createOAuthUserResponse("hello@naver.com", "https://imageurl", "1");

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
                .thenReturn(new AccessTokenResponse(accessToken));
        when(googleOAuthClient.getOAuthUserResponse(accessToken))
                .thenReturn(oAuthUserResponse);

        //when
        final OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo(oAuthUserResponse.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(oAuthUserResponse.getProfileUrl());
        assertThat(response.getGrantType()).isEqualTo("Bearer");
    }

    @ParameterizedTest
    @DisplayName("로그인할 때 지원하지 않는 ProviderName에 대해 예외를 반환한다")
    @ValueSource(strings = { "naver", "kakao", "Google", " ", "" })
    void returnExceptionAboutNotSupportedProviderName(String providerName) {
        // given
        final String authCode = "authcode";

        // when & then
        assertThatThrownBy(() -> authService.login(providerName, authCode))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("auth code를 통해 권한을 받아올 수 없으면 예외를 반환한다")
    void returnExceptionAboutInvalidAuthCode() {
        // given
        final String providerName = "google";
        final String authCode = "authcode";

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
        final String providerName = "google";
        final String authCode = "authcode";
        final String accessToken = "accessToken";

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
        final RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");
        final OAuthMember member = OAuthMember.builder()
                                              .profileUrl("https://oldUrl")
                                              .build();
        authMemberRepository.save(member);

        // when
        final RegisterResponse response = authService.register(request, member.getId());

        //when & then
        assertThat(response.getId()).isNotNull();
        assertThat(member.isRegistered()).isTrue();
        assertThat(member.getProfileUrl()).isEqualTo("https://profileUrl");
    }

    @Test
    @DisplayName("회원가입 시 userId를 사용해 OAuthMember를 조회할 수 없으면 예외를 반환한다")
    void registerFailNotFoundUser() {
        // given
        final RegisterRequest request = new RegisterRequest("https://profileUrl", "김태태");

        // when & then
        assertThatThrownBy(() -> authService.register(request, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("refresh token으로 access token을 재발급한다")
    void refreshToken() {
        // given
        final OAuthMember member = OAuthMember.builder()
                                              .build();
        authMemberRepository.save(member);

        final String refreshToken = jwtTokenProvider.generateTokenInfo(member.getId(), LocalDateTime.now())
                                                    .getRefreshToken();
        member.changeRefreshToken(refreshToken);
        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        final Long memberId = member.getId();

        // when
        final TokenInfo response = authService.refreshToken(request, memberId);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        final TokenInfoResponse parsedToken = jwtTokenProvider.parse(response.getAccessToken());
        assertThat(parsedToken.getId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 refresh token 발급 요청을 하면 예외를 반환한다")
    void requestRefreshTokenIfNotExistsUser() {
        // given
        final Long memberId = 1L;
        final String refreshToken = jwtTokenProvider.generateTokenInfo(memberId, LocalDateTime.now())
                                                    .getRefreshToken();
        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(request, memberId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("입력받은 RefreshToken이 기존에 발급한 Refresh Token과 일치하지 않으면 예외를 반환한다")
    void refreshTokenNotEqualPrevValue() {
        // given
        final OAuthMember member = OAuthMember.builder()
                                              .refreshToken("기존 RefreshToken")
                                              .build();
        authMemberRepository.save(member);

        final Long memberId = member.getId();
        final String refreshToken = jwtTokenProvider.generateTokenInfo(memberId, LocalDateTime.now())
                                                    .getRefreshToken();
        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(request, memberId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
    }

    @Test
    @DisplayName("Refresh Token의 기한이 지났으면 예외를 반환한다")
    void refreshTokenExpired() {
        // given
        final OAuthMember member = OAuthMember.builder()
                                              .build();
        authMemberRepository.save(member);
        final Long memberId = member.getId();
        final String refreshToken = jwtTokenProvider.generateTokenInfo(memberId,
                                                                       LocalDateTime.of(1900, 1, 1, 1, 1))
                                                    .getRefreshToken();
        member.changeRefreshToken(refreshToken);
        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(request, memberId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.TOKEN_TIMEOVER.getMessage());
    }

    private Map<String, String> extractParams(String paramsStr) {
        final Map<String, String> params = new HashMap<>();
        for (String each : paramsStr.split("&")) {
            final String[] keyAndValue = each.split("=");
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

    private OAuthUserResponse createOAuthUserResponse(String email, String profileUrl,
                                                      String idUsingResourceServer) {
        return OAuthUserResponse.builder()
                                .email(email)
                                .profileUrl(profileUrl)
                                .idUsingResourceServer(idUsingResourceServer)
                                .build();
    }
}
