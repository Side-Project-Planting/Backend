package com.example.demo.oauth.google;

import com.example.demo.oauth.OAuthClient;
import com.example.demo.oauth.OAuthProperties;
import com.example.demo.presentation.dto.response.AccessTokenResponse;
import com.example.demo.presentation.dto.response.OAuthUserResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// TODO WebClient로 변경하기
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient {
    private final GoogleProperties properties;

    @Override
    public OAuthProperties getOAuthProperties() {
        return properties;
    }

    @Override
    public AccessTokenResponse getAccessToken(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", properties.getClientId());
        map.add("client_secret", properties.getClientSecret());
        map.add("code", URLDecoder.decode(authCode, StandardCharsets.UTF_8));
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", properties.getRedirectUri());

        HttpEntity<?> entity = new HttpEntity<>(map, headers);
        ResponseEntity<AccessTokenResponse> response =
            new RestTemplate().postForEntity(properties.getTokenUri(), entity, AccessTokenResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new IllegalArgumentException("구글에서 응답을 제대로 받지 못했습니다");
    }

    @Override
    public OAuthUserResponse getOAuthUserResponse(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<OAuthUserResponse> response = new RestTemplate().exchange(
            properties.getUserInfoUri(), HttpMethod.GET, entity, OAuthUserResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new IllegalArgumentException("구글에서 응답을 제대로 받지 못했습니다");
    }
}
