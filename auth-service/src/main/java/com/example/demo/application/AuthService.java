package com.example.demo.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.dto.response.GetAuthorizedUriResponse;
import com.example.demo.application.dto.response.OAuthLoginResponse;
import com.example.demo.application.dto.response.OAuthUserResponse;
import com.example.demo.application.dto.response.RegisterResponse;
import com.example.demo.application.dto.response.TokenRefreshResponse;
import com.example.demo.domain.AuthMemberRepository;
import com.example.demo.domain.OAuthMember;
import com.example.demo.domain.OAuthType;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.factory.RandomStringFactory;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.jwt.TokenInfo;
import com.example.demo.jwt.TokenInfoResponse;
import com.example.demo.oauth.OAuthProvider;
import com.example.demo.oauth.OAuthProviderResolver;
import com.example.demo.presentation.dto.request.RegisterRequest;
import com.example.demo.presentation.dto.request.TokenRefreshRequest;
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

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        return null;
    }
}
