package com.example.demo.presentation.dto.response;

import com.example.demo.domain.OAuthMember;
import com.example.demo.domain.OAuthType;
import com.fasterxml.jackson.annotation.JsonProperty;
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