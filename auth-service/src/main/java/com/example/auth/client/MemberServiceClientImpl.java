package com.example.auth.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.auth.client.dto.MemberRegisterRequest;
import com.example.auth.client.dto.MemberRegisterResponse;

@Component
public class MemberServiceClientImpl implements MemberServiceClient {
    private final WebClient webClient;

    public MemberServiceClientImpl() {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8001")
            .build();
    }


    public MemberRegisterResponse register(MemberRegisterRequest request) {
        return webClient.post()
            .uri("/members")
            .header("X-User-Id", "-1")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(MemberRegisterResponse.class)
            .block();
    }
}
