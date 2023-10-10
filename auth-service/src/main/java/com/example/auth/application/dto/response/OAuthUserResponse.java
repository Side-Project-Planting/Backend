package com.example.auth.application.dto.response;

import com.example.auth.domain.OAuthMember;
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

    public static OAuthMember create(OAuthUserResponse response, OAuthType oAuthType) {
        return OAuthMember.builder()
            .idUsingResourceServer(response.getIdUsingResourceServer())
            .oAuthType(oAuthType)
            .email(response.getEmail())
            .profileUrl(response.getProfileUrl())
            .build();
    }

    public OAuthMember toEntity(OAuthType oAuthType) {
        return OAuthMember.builder()
            .idUsingResourceServer(this.getIdUsingResourceServer())
            .email(this.getEmail())
            .profileUrl(this.getProfileUrl())
            .oAuthType(oAuthType)
            .build();
    }
}
