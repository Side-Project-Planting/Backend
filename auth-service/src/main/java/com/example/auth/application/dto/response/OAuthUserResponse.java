package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthInfo;
import com.example.auth.domain.OAuthType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OAuthUserResponse {
    @JsonProperty("picture")
    private String profileUrl;

    @JsonProperty("email")
    private String email;

    @JsonProperty("sub")
    private String idUsingResourceServer;

    @Builder
    private OAuthUserResponse(String profileUrl, String email, String idUsingResourceServer) {
        this.profileUrl = profileUrl;
        this.email = email;
        this.idUsingResourceServer = idUsingResourceServer;
    }

    public OAuthInfo toEntity(OAuthType oAuthType) {
        return OAuthInfo.builder()
            .idUsingResourceServer(idUsingResourceServer)
            .email(email)
            .oAuthType(oAuthType)
            .build();
    }
}
