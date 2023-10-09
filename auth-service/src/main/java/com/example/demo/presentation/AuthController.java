package com.example.demo.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.AuthService;
import com.example.demo.application.dto.response.GetAuthorizedUriResponse;
import com.example.demo.application.dto.response.OAuthLoginResponse;
import com.example.demo.application.dto.response.RegisterResponse;
import com.example.demo.jwt.TokenInfo;
import com.example.demo.jwt.TokenInfoResponse;
import com.example.demo.presentation.dto.request.OAuthLoginRequest;
import com.example.demo.presentation.dto.request.RegisterRequest;
import com.example.demo.presentation.dto.request.TokenRefreshRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<GetAuthorizedUriResponse> getAuthorizedUri(@PathVariable String provider) {
        return ResponseEntity.ok(authService.getAuthorizedUri(provider));
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<OAuthLoginResponse> oauthLogin(@PathVariable String provider,
                                                         @RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(provider, request.getAuthCode()));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request,
                                                     @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.created(URI.create("/members/" + userId))
            .body(authService.register(request, userId));
    }

    @GetMapping("/auth/parse")
    public ResponseEntity<TokenInfoResponse> parseToken(@RequestParam String token) {
        return ResponseEntity.ok().body(authService.parse(token));
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<TokenInfo> refreshToken(@RequestBody TokenRefreshRequest request,
                                                  @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok().body(authService.refreshToken(request, userId));
    }
}
