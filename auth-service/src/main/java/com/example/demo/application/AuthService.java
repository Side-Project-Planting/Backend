package com.example.demo.application;

import com.example.demo.oauth.OAuthProvider;
import com.example.demo.presentation.dto.response.GetAuthorizedUrlResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final List<OAuthProvider> oAuthProviders;

    /**
     * 입력받은 providerName을 사용해 해당되는 OAuthProvider를 찾는다.
     * 반환된 OAuthProvider은 Authorized URL을 만들어 반환한다.
     *
     */
    public GetAuthorizedUrlResponse getAuthorizedUri(String providerName) {
        OAuthProvider oAuthProvider = oAuthProviders.stream()
            .filter(provider -> provider.match(providerName))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매치되지 않는 타입입니다"));

        return new GetAuthorizedUrlResponse(oAuthProvider.getAuthorizedUriWithParams("랜덤값"));
    }
}
