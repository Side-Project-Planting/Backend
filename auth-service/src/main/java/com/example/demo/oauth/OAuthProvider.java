package com.example.demo.oauth;

public interface OAuthProvider {
    boolean match(String name);

    String getAuthorizedUriEndpoint();

    String getClientId();

    String getRedirectUri();

    String[] getScope();

    String getResponseType();

    /**
     * 입력한 값들을 조합해 클라이언트에게 반환할 AuthorizedUri을 만든다.
     *
     * @param state
     * @return
     */
    default String getAuthorizedUriWithParams(String state) {
        return getAuthorizedUriEndpoint() + "?" +
            "clientId=" + getClientId() +
            "&redirect_uri=" + getRedirectUri() +
            "&scope=" + String.join(",", getScope()) +
            "&response_type=" + getResponseType() +
            "&state=" + state;
    }
}
