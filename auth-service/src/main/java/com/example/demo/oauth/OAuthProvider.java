package com.example.demo.oauth;

import com.example.demo.domain.OAuthMember;
import com.example.demo.domain.OAuthType;
import com.example.demo.presentation.dto.response.OAuthUserResponse;
import java.util.Objects;

public interface OAuthProvider {

    OAuthType getOAuthType();

    OAuthProperties getOAuthProperties();


    /**
     * authCode를 사용해 Access Token을 받아온 뒤, Resource Server에서 여러 정보를 가져온다.
     */
    OAuthUserResponse getOAuthUserResponse(String authCode);

    /**
     * 해당 Provider가 처리할 수 있는지 확인한다.
     */
    default boolean match(String name) {
        OAuthType type = getOAuthType();
        return Objects.equals(type.getText(), name);
    }

    /**
     * 입력한 값들을 조합해 클라이언트에게 반환할 AuthorizedUri을 만든다.
     */
    default String getAuthorizedUriWithParams(String state) {
        OAuthProperties properties = getOAuthProperties();
        return properties.getAuthorizedUriEndpoint() + "?" +
            "client_id=" + properties.getClientId() +
            "&redirect_uri=" + properties.getRedirectUri() +
            "&scope=" + String.join(",", properties.getScope()) +
            "&response_type=" + properties.getResponseType() +
            "&state=" + state;
    }
}
