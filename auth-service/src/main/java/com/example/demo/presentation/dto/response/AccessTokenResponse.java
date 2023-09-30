package com.example.demo.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}