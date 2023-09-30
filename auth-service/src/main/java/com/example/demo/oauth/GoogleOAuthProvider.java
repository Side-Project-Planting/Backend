package com.example.demo.oauth;

import com.example.demo.domain.OAuthMember;
import com.example.demo.domain.OAuthType;
import com.example.demo.presentation.dto.response.AccessTokenResponse;
import com.example.demo.presentation.dto.response.OAuthUserResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {
    private static final String NAME = "google";

    private final GoogleProperties googleProperties;

    @Override
    public boolean match(String name) {
        return Objects.equals(NAME, name);
    }

    @Override
    public String getAuthorizedUriEndpoint() {
        return googleProperties.getAuthorizedUriEndpoint();
    }

    @Override
    public String getClientId() {
        return googleProperties.getClientId();
    }

    @Override
    public String getRedirectUri() {
        return googleProperties.getRedirectUri();
    }

    @Override
    public String[] getScope() {
        return googleProperties.getScope();
    }

    @Override
    public String getResponseType() {
        return googleProperties.getResponseType();
    }

    @Override
    public String getTokenUri() {
        return googleProperties.getTokenUri();
    }

    @Override
    public OAuthMember createAuthMember(String authCode) {
        ResponseEntity<AccessTokenResponse> responseWithAccessToken = getAccessToken(authCode);
        if (responseWithAccessToken.getStatusCode().is2xxSuccessful()) {
            String accessToken = responseWithAccessToken.getBody().getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(headers);

            ResponseEntity<OAuthUserResponse> result =
                new RestTemplate().exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity,
                    OAuthUserResponse.class);

            OAuthUserResponse info = result.getBody();
            return OAuthUserResponse.create(info, OAuthType.GOOGLE);
        }
        throw new IllegalArgumentException("요청에 실패했습니다");
    }

    private ResponseEntity<AccessTokenResponse> getAccessToken(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", getClientId());
        map.add("client_secret", googleProperties.getClientSecret());
        map.add("code", URLDecoder.decode(authCode, StandardCharsets.UTF_8));
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", getRedirectUri());

        HttpEntity<?> entity = new HttpEntity<>(map, headers);
        return new RestTemplate().postForEntity(getTokenUri(), entity, AccessTokenResponse.class);
    }
}