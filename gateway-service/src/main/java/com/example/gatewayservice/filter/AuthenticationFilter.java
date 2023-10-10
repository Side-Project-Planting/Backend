package com.example.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.example.gatewayservice.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

// TODO 테스트 코드가 필요합니다. 해당 부분은 통합테스트가 필요하다 느껴져, 하위 서버를 만든 뒤 테스트를 진행할 생각입니다.
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final JwtValidator jwtValidator;

    /**
     * 요청에 Bearer + JWT 토큰이 함께 들어온 경우에는, 토큰을 검증한 뒤 응답에 X-User-Id : subject 헤더를 추가한 뒤 다음 필터를 실행한다.
     * 만약 토큰이 존재하지 않았다면 다음 필터를 실행한다. 이 경우 사용자가 임의로 값을 설정할 수 없도록 X-User-Id를 제거하는 과정을 갖는다.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tokenBeforeProcessing = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (tokenBeforeProcessing != null && tokenBeforeProcessing.startsWith("Bearer ")) {
            String token = tokenBeforeProcessing.substring(7);
            if (!jwtValidator.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }

            String subject = jwtValidator.getSubject(token);
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", subject)
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .headers(headers -> headers.remove("X-User-Id"))
            .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

}
