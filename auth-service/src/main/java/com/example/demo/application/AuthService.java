package com.example.demo.application;

import com.example.demo.domain.AuthMemberRepository;
import com.example.demo.domain.OAuthMember;
import com.example.demo.factory.RandomStringFactory;
import com.example.demo.oauth.OAuthProvider;
import com.example.demo.presentation.dto.response.GetAuthorizedUrlResponse;
import com.example.demo.presentation.dto.response.OAuthLoginResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final List<OAuthProvider> oAuthProviders;
    private final AuthMemberRepository authMemberRepository;
    private final RandomStringFactory randomStringFactory;

    /**
     * 입력받은 providerName을 사용해 해당되는 OAuthProvider를 찾는다.
     * 반환된 OAuthProvider은 Authorized URL을 만들어 반환한다.
     */
    public GetAuthorizedUrlResponse getAuthorizedUri(String providerName) {
        OAuthProvider oAuthProvider = findProvider(providerName);
        String state = randomStringFactory.create();
        return new GetAuthorizedUrlResponse(oAuthProvider.getAuthorizedUriWithParams(state));
    }

    @Transactional
    public OAuthLoginResponse login(String providerName, String authCode) {
        OAuthProvider oAuthProvider = findProvider(providerName);
        OAuthMember oAuthMember = oAuthProvider.createAuthMember(authCode);

        Optional<OAuthMember> oAuthMemberOpt = authMemberRepository.findByIdUsingResourceServerAndType(
            oAuthMember.getIdUsingResourceServer(), oAuthMember.getType());

        if (oAuthMemberOpt.isEmpty()) {
            oAuthMember = authMemberRepository.save(oAuthMember);
        } else {
            oAuthMember = oAuthMemberOpt.get();
        }

        return OAuthLoginResponse.builder()
            .accessToken("액세스토큰")
            .refreshToken("리프레쉬")
            .profileUrl(oAuthMember.getProfileUrl())
            .email(oAuthMember.getEmail())
            .isNew(true)
            .build();
    }

    private OAuthProvider findProvider(String providerName) {
        return oAuthProviders.stream()
            .filter(provider -> provider.match(providerName))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매치되지 않는 타입입니다"));
    }
}
