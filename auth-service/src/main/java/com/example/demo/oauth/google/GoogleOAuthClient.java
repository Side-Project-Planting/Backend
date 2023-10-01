package com.example.demo.oauth.google;

import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.oauth.OAuthClient;
import com.example.demo.oauth.OAuthProperties;
import com.example.demo.application.dto.response.AccessTokenResponse;
import com.example.demo.application.dto.response.OAuthUserResponse;
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

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("code", authCode);
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("grant_type", "authorization_code");


        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<AccessTokenResponse> response =
            new RestTemplate().postForEntity(properties.getTokenUri(), request, AccessTokenResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new ApiException(ErrorCode.ACCESS_TOKEN_FETCH_FAIL);
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
        throw new ApiException(ErrorCode.USER_INFO_FETCH_FAIL);
    }
}
