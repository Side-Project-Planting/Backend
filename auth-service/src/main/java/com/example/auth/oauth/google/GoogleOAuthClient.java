package com.example.auth.oauth.google;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.auth.application.dto.response.AccessTokenResponse;
import com.example.auth.application.dto.response.OAuthUserResponse;
import com.example.auth.exception.ApiException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.oauth.OAuthClient;
import com.example.auth.oauth.OAuthProperties;

import lombok.RequiredArgsConstructor;

// TODO WebClient로 변경하기
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient {
    private final OAuthProperties properties;

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
