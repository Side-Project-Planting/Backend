package com.example.demo.application;

import com.example.demo.domain.AuthMemberRepository;
import com.example.demo.oauth.OAuthProvider;
import com.example.demo.presentation.dto.response.AccessTokenResponse;
import com.example.demo.presentation.dto.response.GetAuthorizedUrlResponse;
import com.example.demo.presentation.dto.response.OAuthLoginResponse;
import com.example.demo.presentation.dto.response.OAuthUserInfo;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final List<OAuthProvider> oAuthProviders;
    private final AuthMemberRepository authMemberRepository;


    /**
     * 입력받은 providerName을 사용해 해당되는 OAuthProvider를 찾는다.
     * 반환된 OAuthProvider은 Authorized URL을 만들어 반환한다.
     */
    public GetAuthorizedUrlResponse getAuthorizedUri(String providerName) {
        OAuthProvider oAuthProvider = findProvider(providerName);
        return new GetAuthorizedUrlResponse(oAuthProvider.getAuthorizedUriWithParams("랜덤값"));
    }

    public OAuthLoginResponse login(String providerName, String authCode) {
        OAuthProvider oAuthProvider = findProvider(providerName);

        ResponseEntity<AccessTokenResponse> responseEntity = getAccessToken(authCode, oAuthProvider);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // 로그인 성공 access Token으로 유의미한 행동
            OAuthUserInfo oAuthUserInfo = getOAuthUserInfo(responseEntity);
            // TODO DB에서 User정보를 찾는다
            // TODO 우리 서비스의 엑세스 토큰을 만든다

            return OAuthLoginResponse.builder()
                .accessToken("액세스토큰")
                .refreshToken("리프레쉬")
                .profileUrl(oAuthUserInfo.getProfileUrl())
                .email(oAuthUserInfo.getEmail())
                .isNew(true)
                .build();
        } else {
            throw new IllegalArgumentException("요청에 실패했습니다.");
        }
    }

    private static OAuthUserInfo getOAuthUserInfo(ResponseEntity<AccessTokenResponse> responseEntity) {
        String accessToken = responseEntity.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<OAuthUserInfo> result =
            new RestTemplate().exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity,
                OAuthUserInfo.class);

        return result.getBody();
    }

    private ResponseEntity<AccessTokenResponse> getAccessToken(String authCode,
                                                               OAuthProvider oAuthProvider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", oAuthProvider.getClientId());
        map.add("client_secret", oAuthProvider.getClientSecret());
        map.add("code", URLDecoder.decode(authCode, StandardCharsets.UTF_8));
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", oAuthProvider.getRedirectUri());

        HttpEntity<?> entity = new HttpEntity<>(map, headers);
        return new RestTemplate().postForEntity(oAuthProvider.getTokenUri(), entity, AccessTokenResponse.class);
    }

    private OAuthProvider findProvider(String providerName) {
        return oAuthProviders.stream()
            .filter(provider -> provider.match(providerName))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매치되지 않는 타입입니다"));
    }
}
