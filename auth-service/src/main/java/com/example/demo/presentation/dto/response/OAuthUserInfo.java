package com.example.demo.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OAuthUserInfo {
    @JsonProperty("picture")
    private String profileUrl;

    @JsonProperty("email")
    private String email;

    @JsonProperty("sub")
    private String sub;
}