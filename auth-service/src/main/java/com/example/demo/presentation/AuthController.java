package com.example.demo.presentation;

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

    @GetMapping("/oauth/{provider}/authorized-url")
    public ResponseEntity<Object> getAuthorizedUrl(@PathVariable String provider) {
        // 링크를 만들어서 보내준다
        return null;
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<Object> oauthLogin(@PathVariable String provider, @RequestBody String authCode) {
        // authCode를 받아서 이런저런 정보들을 반환한다
        return null;
    }

}
