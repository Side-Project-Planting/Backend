package com.example.auth.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.auth.exception.ApiException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.factory.RandomStringFactory;
import com.example.auth.jwt.JwtTokenProvider;
import com.example.auth.jwt.TokenInfo;
import com.example.auth.jwt.TokenInfoResponse;
import com.example.auth.oauth.OAuthProvider;
import com.example.auth.oauth.OAuthProviderResolver;
import com.example.auth.presentation.dto.request.RegisterRequest;
import com.example.auth.presentation.dto.request.TokenRefreshRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final OAuthProviderResolver oAuthProviderResolver;
    private final AuthInfoRepository authInfoRepository;
    private final RandomStringFactory randomStringFactory;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberServiceClient memberServiceClient;

    /**
     * 입력받은 providerName을 사용해 해당되는 OAuthProvider를 찾는다.
     * 반환된 OAuthProvider은 Authorized URL을 만들어 반환한다.
     */
    public GetAuthorizedUriResponse getAuthorizedUri(String providerName) {
        OAuthProvider oAuthProvider = oAuthProviderResolver.find(providerName);
        String state = randomStringFactory.create();
        return new GetAuthorizedUriResponse(oAuthProvider.getAuthorizedUriWithParams(state));
    }

    @Transactional
    public OAuthLoginResponse login(String providerName, String authCode) {
        OAuthProvider oAuthProvider = oAuthProviderResolver.find(providerName);
        OAuthUserResponse response = oAuthProvider.getOAuthUserResponse(authCode);
        OAuthInfo oAuthInfo = retrieveOrCreateMemberUsingAuthCode(oAuthProvider.getOAuthType(), response);

        TokenInfo tokenInfo = jwtTokenProvider.generateTokenInfo(oAuthInfo.getId(), LocalDateTime.now());
        // TODO 현재는 Auth Service의 ID를 담아서 토큰을 생성하는중

        oAuthInfo.changeRefreshToken(tokenInfo.getRefreshToken());
        return OAuthLoginResponse.create(oAuthInfo, tokenInfo, response.getProfileUrl());
    }

    private OAuthInfo retrieveOrCreateMemberUsingAuthCode(OAuthType type, OAuthUserResponse response) {
        Optional<OAuthInfo> oAuthInfoOpt = authInfoRepository.findByIdUsingResourceServerAndType(
            response.getIdUsingResourceServer(), type);

        if (oAuthInfoOpt.isEmpty()) {
            OAuthInfo oAuthInfo = response.toEntity(type);
            return authInfoRepository.save(oAuthInfo);
        }
        return oAuthInfoOpt.get();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request, Long userId) {
        OAuthInfo info = authInfoRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        MemberRegisterRequest requestUsingMemberService =
            MemberRegisterRequest.create(request.getProfileUrl(), request.getName(), info.getEmail());
        MemberRegisterResponse response = memberServiceClient.register(requestUsingMemberService);

        info.init();
        return new RegisterResponse(response.getId());
    }

    public TokenInfoResponse parse(String token) {
        return jwtTokenProvider.parse(token);
    }

    @Transactional
    public TokenInfo refreshToken(TokenRefreshRequest request, Long userId) {
        OAuthInfo info = authInfoRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (!info.isRefreshTokenMatching(request.getRefreshToken())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (jwtTokenProvider.isTokenExpired(info.getRefreshToken())) {
            throw new ApiException(ErrorCode.TOKEN_TIMEOVER);
        }

        TokenInfo generatedTokenInfo = jwtTokenProvider.generateTokenInfo(info.getId(), LocalDateTime.now());

        info.changeRefreshToken(generatedTokenInfo.getRefreshToken());
        return generatedTokenInfo;
    }
}
