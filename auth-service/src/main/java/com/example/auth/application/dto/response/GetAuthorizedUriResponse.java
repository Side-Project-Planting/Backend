package com.example.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAuthorizedUriResponse {
    @Schema(description = "OAuth 로그인에 필요한 Redirect Uri", nullable = false,
        example = "https://accounts.google.com/o/oauth2/auth" +
            "?client_id=276707814597-4g4ppito7ipv2el7ku323h6ffccsfqgs.apps.googleusercontent.com" +
            "&redirect_uri=http://localhost:8443/login/oauth2/code/google" +
            "&scope=email" +
            "&response_type=code" +
            "&state=RxylSzWRD8AQXtgYA3igyVCpZoUmy39esJr9useN18o")
    private String authorizedUri;
}
