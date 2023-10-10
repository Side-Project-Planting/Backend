package com.example.auth.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AuthMemberRepository authMemberRepository;
    private final RandomStringFactory randomStringFactory;
    private final JwtTokenProvider jwtTokenProvider;

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
        OAuthMember oAuthMember = retrieveOrCreateMemberUsingAuthCode(oAuthProvider.getOAuthType(), response);

        TokenInfo tokenInfo = jwtTokenProvider.generateTokenInfo(oAuthMember.getId(), LocalDateTime.now());
        oAuthMember.changeRefreshToken(tokenInfo.getRefreshToken());
        return OAuthLoginResponse.create(oAuthMember, tokenInfo);
    }

    private OAuthMember retrieveOrCreateMemberUsingAuthCode(OAuthType type, OAuthUserResponse response) {
        Optional<OAuthMember> oAuthMemberOpt = authMemberRepository.findByIdUsingResourceServerAndType(
            response.getIdUsingResourceServer(), type);

        if (oAuthMemberOpt.isEmpty()) {
            OAuthMember oAuthMember = response.toEntity(type);
            return authMemberRepository.save(oAuthMember);
        }
        return oAuthMemberOpt.get();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request, Long userId) {
        // 입력한 값을 DB에 저장하고, Member Service에 요청을 보낸다
        Optional<OAuthMember> memberOpt = authMemberRepository.findById(userId);
        if (memberOpt.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        OAuthMember member = memberOpt.get();
        member.init(request.getProfileUrl());

        // TODO 외부 서버인 Member Service를 호출해서 Member를 생성하는 기능이 추가되어야 합니다
        return new RegisterResponse(member.getId());
    }

    public TokenInfoResponse parse(String token) {
        return jwtTokenProvider.parse(token);
    }

    @Transactional
    public TokenInfo refreshToken(TokenRefreshRequest request, Long userId) {
        Optional<OAuthMember> memberOpt = authMemberRepository.findById(userId);
        if (memberOpt.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        OAuthMember member = memberOpt.get();
        if (!member.isRefreshTokenMatching(request.getRefreshToken())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (jwtTokenProvider.isTokenExpired(member.getRefreshToken())) {
            throw new ApiException(ErrorCode.TOKEN_TIMEOVER);
        }

        TokenInfo generatedTokenInfo = jwtTokenProvider.generateTokenInfo(member.getId(), LocalDateTime.now());

        member.changeRefreshToken(generatedTokenInfo.getRefreshToken());
        return generatedTokenInfo;
    }
}
