package com.example.demo.oauth;

// TODO 추후 yml 값을 받아오도록 변경
public class GoogleProperties {
    public String getAuthorizedUriEndpoint() {
        return "https://accounts.google.com/o/oauth2/auth";
    }

    public String getClientId() {
        return "클라이언트아이디";
    }

    public String getRedirectUri() {
        return "리다이렉트";
    }

    public String[] getScope() {
        return new String[] {"스코프"};
    }

    public String getResponseType() {
        return "code";
    }
}
