package com.example.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.auth.client.MemberServiceClient;
import com.example.auth.client.dto.MemberRegisterRequest;
import com.example.auth.client.dto.MemberRegisterResponse;
import com.example.auth.domain.AuthInfoRepository;
import com.example.auth.domain.OAuthInfo;
import com.example.auth.domain.OAuthType;
import com.example.auth.domain.member.Member;
import com.example.auth.domain.member.MemberRepository;
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
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@DisplayName("AuthService 통합테스트")
class AuthServiceTest {
    private static Long globalMemberId = 123L;

    AuthService authService;

    @Autowired
    AuthInfoRepository authInfoRepository;

    @Autowired
    MemberRepository memberRepository;

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

    @MockBean
    MemberServiceClient memberServiceClient;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        provider = new GoogleOAuthProvider(oAuthProperties, googleOAuthClient);
        resolver = new OAuthProviderResolver(List.of(provider));
        authService = new AuthService(resolver, authInfoRepository, memberRepository, factory, jwtTokenProvider,
            memberServiceClient);
    }

    @Test
    @DisplayName("google의 AuthorizedUrl을 가져온다")
    void getGoogleAuthorizedUrl() {
        //when
        String authorizedUrl = authService.getAuthorizedUri("google");

        // then
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
    @DisplayName("처리할 수 없는 ProviderName으로는 AuthorizedUri를 가져올 수 없다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
    void cantGetAuthorizedUrlAboutNotSupportedProviderName(String providerName) {
        assertThatThrownBy(() -> authService.getAuthorizedUri(providerName))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원가입되지 않은 사용자가 OAuth 로그인을 하면 email, ProfileUrl, authorizedToken를 반환한다")
    void loginSuccessIfNotRegistered() {
        // given
        String providerName = "google";
        String authCode = "authcode";
        String accessTokenUsingProvider = "accessToken";
        String idReceivedProvider = "1";
        OAuthInfo info = createOAuthInfo(idReceivedProvider, null);

        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenReturn(new AccessTokenResponse(accessTokenUsingProvider));
        OAuthUserResponse responseReceivedProvider =
            createOAuthUserResponse("hello@naver.com", "https://imageurl", idReceivedProvider);
        when(googleOAuthClient.getOAuthUserResponse(accessTokenUsingProvider))
            .thenReturn(responseReceivedProvider);

        //when
        OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.isRegistered()).isFalse();

        assertThat(response.getEmail()).isEqualTo(responseReceivedProvider.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(responseReceivedProvider.getProfileUrl());
        assertThat(response.getAuthId()).isEqualTo(info.getId());
        assertThat(info.getAuthorizedToken()).isNotBlank();

        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();
        assertThat(response.getGrantType()).isNull();
    }

    @Test
    @DisplayName("회원가입된 사용자가 OAuth 로그인을 하면 access token, refresh token을 포함하여 반환한다")
    void loginSuccessIfRegistered() {
        // given
        String providerName = "google";
        String authCode = "authcode";
        String accessTokenUsingProvider = "accessToken";
        String prevRefreshToken = "기존리프레쉬토큰";
        long memberId = globalMemberId++;
        String idUsingResourceServer = "1";

        Member member = createMember(memberId, prevRefreshToken);
        OAuthInfo info = createOAuthInfo(idUsingResourceServer, member);


        // stub
        when(googleOAuthClient.getAccessToken(anyString()))
            .thenReturn(new AccessTokenResponse(accessTokenUsingProvider));
        OAuthUserResponse oAuthUserResponse =
            createOAuthUserResponse("hello@naver.com", "https://imageurl", idUsingResourceServer);
        when(googleOAuthClient.getOAuthUserResponse(accessTokenUsingProvider))
            .thenReturn(oAuthUserResponse);

        //when
        OAuthLoginResponse response = authService.login(providerName, authCode);

        // then
        assertThat(response.isRegistered()).isTrue();

        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
        assertThat(response.getGrantType()).isEqualTo("Bearer");

        assertThat(response.getEmail()).isEqualTo(oAuthUserResponse.getEmail());
        assertThat(response.getProfileUrl()).isEqualTo(oAuthUserResponse.getProfileUrl());
        assertThat(response.getAuthId()).isNull();
        assertThat(response.getAuthorizedToken()).isBlank();

        assertThat(member.getRefreshToken()).isNotEqualTo(prevRefreshToken);
    }

    @ParameterizedTest
    @DisplayName("로그인할 때 지원하지 않는 ProviderName에 대해 예외를 반환한다")
    @ValueSource(strings = {"naver", "kakao", "Google", " ", ""})
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
    @DisplayName("Member를 등록한다")
    void testMemberRegister() {
        // given
        String authToken = "인가_토큰";
        OAuthInfo info = OAuthInfo.builder().authorizedToken(authToken).build();
        authInfoRepository.save(info);

        RegisterRequest request = createRegisterRequest("https://profile", "김태태", info.getId(), authToken);

        long registeredId = 2L;
        MemberRegisterResponse memberServiceResponse = MemberRegisterResponse.builder()
            .id(registeredId)
            .build();

        // stub
        when(memberServiceClient.register(any(MemberRegisterRequest.class)))
            .thenReturn(memberServiceResponse);

        // when
        RegisterResponse response = authService.register(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        Member member = memberRepository.findById(response.getId()).get();
        assertThat(member.getRefreshToken()).isEqualTo(response.getRefreshToken());
    }

    @Test
    @DisplayName("회원가입 시 userId를 사용해 OAuthInfo를 조회할 수 없으면 예외를 반환한다")
    void registerFailNotFoundUser() {
        // given
        Long notRegisteredId = 12451L;
        RegisterRequest request = createRegisterRequest("https://profileUrl",
            "김태태", notRegisteredId, "인가_코드");

        // when & then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTH_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원가입 시 발급해줬던 AuthorizedToken을 입력해야 한다")
    void registerFailInvalidAuthorizedToken() {
        // given
        OAuthInfo info = OAuthInfo.builder().authorizedToken("인가_토큰").build();
        authInfoRepository.save(info);

        RegisterRequest request = createRegisterRequest("https://profileUrl",
            "김태태", info.getId(), "잘못된_인가_코드");

        // when & then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TOKEN_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("refresh token으로 access token을 재발급한다")
    void refreshToken() {
        // given
        Member member = Member.builder().id(globalMemberId++).refreshToken("기존_리프레쉬_토큰").build();
        String refreshToken = jwtTokenProvider.generateTokenInfo(member.getId(), LocalDateTime.now()).getRefreshToken();
        member.changeRefreshToken(refreshToken);
        em.persist(member);
        // TODO 집가서 JPA 책 살펴보기. 왜 member save -> member.changeRefreshToken 하면 이후에 flush, clear 해도 변경을 못잡는거지?
        Long memberId = member.getId();


        // when
        TokenInfo response = authService.refreshToken(refreshToken);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getGrantType()).isEqualTo("Bearer");

        TokenInfoResponse parsedToken = jwtTokenProvider.parse(response.getAccessToken());
        assertThat(parsedToken.getId()).isEqualTo(memberId);

        assertThat(member.getRefreshToken()).isNotEqualTo("기존_리프레쉬_토큰");
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 refresh token 발급 요청을 하면 예외를 반환한다")
    void requestRefreshTokenIfNotExistsUser() {
        // given
        Long notRegisteredMemberId = globalMemberId++;
        String refreshToken = jwtTokenProvider.generateTokenInfo(notRegisteredMemberId, LocalDateTime.now())
            .getRefreshToken();

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SERVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("입력받은 RefreshToken이 기존에 발급한 Refresh Token과 일치하지 않으면 예외를 반환한다")
    void refreshTokenNotEqualPrevValue() {
        // given
        Member member = createMember(1L, "진짜_리프레쉬_토큰");
        String fakeRefreshToken = jwtTokenProvider.generateTokenInfo(member.getId(), LocalDateTime.now())
            .getRefreshToken();

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(fakeRefreshToken))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TOKEN_ID_INVALID.getMessage());
    }

    @Test
    @DisplayName("Refresh Token의 기한이 지났으면 예외를 반환한다")
    void refreshTokenExpired() {
        // given
        Long memberId = globalMemberId++;
        String refreshToken = jwtTokenProvider.generateTokenInfo(memberId,
                LocalDateTime.of(1900, 1, 1, 1, 1))
            .getRefreshToken();

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
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

    private Member createMember(long memberId, String refreshToken) {
        Member member = Member.builder().id(memberId).refreshToken(refreshToken).build();
        memberRepository.save(member);
        return member;
    }

    private OAuthInfo createOAuthInfo(String idUsingResourceServer, Member member) {
        OAuthInfo info = OAuthInfo.builder()
            .oAuthType(OAuthType.GOOGLE)
            .idUsingResourceServer(idUsingResourceServer)
            .build();
        info.init(member);
        authInfoRepository.save(info);
        return info;
    }

    private OAuthUserResponse createOAuthUserResponse(String email, String profileUrl,
                                                      String idUsingResourceServer) {
        return OAuthUserResponse.builder()
            .email(email)
            .profileUrl(profileUrl)
            .idUsingResourceServer(idUsingResourceServer)
            .build();
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
