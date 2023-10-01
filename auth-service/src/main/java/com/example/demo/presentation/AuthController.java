package com.example.demo.presentation;

import com.example.demo.application.AuthService;
import com.example.demo.presentation.dto.response.ApiResponse;
import com.example.demo.presentation.dto.response.GetAuthorizedUriResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;

    // TODO 삭제해야 하는 로직입니다.
    //  Auth Code를 확인하기 위해 임의로 설정한 URL입니다.
    @GetMapping("/login/oauth2/code/google")
    public String showAuthCode(String code) {
        return code;
    }

    @GetMapping("/oauth/{provider}/authorized-uri")
    public ApiResponse<GetAuthorizedUriResponse> getAuthorizedUri(@PathVariable String provider) {
        return ApiResponse.ok(authService.getAuthorizedUri(provider));
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<Object> oauthLogin(@PathVariable String provider, @RequestBody String authCode) {
        // authCode를 받아서 이런저런 정보들을 반환한다
        return null;
    }

}
