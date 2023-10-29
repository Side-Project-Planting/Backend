package com.example.auth.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.auth.client.dto.MemberRegisterRequest;
import com.example.auth.client.dto.MemberRegisterResponse;
import com.example.auth.exception.ApiException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ErrorResponse;
import com.example.auth.exception.MicroserviceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MemberServiceClientImpl implements MemberServiceClient {
    private final WebClient webClient;

    public MemberServiceClientImpl() {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8001") // TODO 분리하기
            .build();
    }


    public MemberRegisterResponse register(MemberRegisterRequest request) {
        return webClient.post()
            .uri("/members") // TODO 역시 분리하기
            .header("X-User-Id", "-1")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                log.error("[Auth Service] 에러: 회원가입 요청 시 {}번 응답이 왔습니다", clientResponse.statusCode().value());
                return clientResponse.bodyToMono(ErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new MicroserviceException(clientResponse.statusCode(), errorResponse.getMessage())));
            })
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                log.error("[Auth Service] 하위 서버 에러: 회원가입 요청 시 {}번 응답이 왔습니다", clientResponse.statusCode().value());
                return Mono.error(new ApiException(ErrorCode.SERVER_ERROR));
            })
            .bodyToMono(MemberRegisterResponse.class)
            .block();
    }
}
