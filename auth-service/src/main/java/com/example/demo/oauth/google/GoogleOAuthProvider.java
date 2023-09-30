package com.example.demo.oauth.google;

import com.example.demo.domain.OAuthType;
import com.example.demo.oauth.OAuthProperties;
import com.example.demo.oauth.OAuthProvider;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {
    private static final OAuthType TYPE = OAuthType.GOOGLE;

    private final GoogleProperties googleProperties;

    @Override
    public OAuthType getOAuthType() {
        return TYPE;
    }

    @Override
    public OAuthProperties getOAuthProperties() {
        return googleProperties;
    }

    @Override
    public OAuthUserResponse getOAuthUserResponse(String authCode) {
        ResponseEntity<AccessTokenResponse> responseWithAccessToken = getAccessToken(authCode);
        if (responseWithAccessToken.getStatusCode().is2xxSuccessful()) {
            String accessToken = responseWithAccessToken.getBody().getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(headers);

            ResponseEntity<OAuthUserResponse> result =
                new RestTemplate().exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity,
                    OAuthUserResponse.class);

            return result.getBody();
        }
        throw new IllegalArgumentException("요청에 실패했습니다");
    }

    private ResponseEntity<AccessTokenResponse> getAccessToken(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", googleProperties.getClientId());
        map.add("client_secret", googleProperties.getClientSecret());
        map.add("code", URLDecoder.decode(authCode, StandardCharsets.UTF_8));
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", googleProperties.getRedirectUri());

        HttpEntity<?> entity = new HttpEntity<>(map, headers);
        return new RestTemplate().postForEntity(googleProperties.getTokenUri(), entity, AccessTokenResponse.class);
    }
}