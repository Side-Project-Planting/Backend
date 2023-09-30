package com.example.demo.oauth;

import com.example.demo.domain.OAuthMember;

public interface OAuthProvider {
    boolean match(String name);

    String getAuthorizedUriEndpoint();

    String getClientId();

    String getRedirectUri();

    String[] getScope();

    String getResponseType();

    String getTokenUri();


    /**
     * authCode를 사용해 Access Token을 받아온 뒤, Resource Server에서 여러 정보를 가져온다.
     *
     * @param authCode
     * @return
     */
    OAuthMember createAuthMember(String authCode);

    /**
     * 입력한 값들을 조합해 클라이언트에게 반환할 AuthorizedUri을 만든다.
     *
     * @param state
     * @return
     */
    default String getAuthorizedUriWithParams(String state) {
        return getAuthorizedUriEndpoint() + "?" +
            "client_id=" + getClientId() +
            "&redirect_uri=" + getRedirectUri() +
            "&scope=" + String.join(",", getScope()) +
            "&response_type=" + getResponseType() +
            "&state=" + state;
    }
}
