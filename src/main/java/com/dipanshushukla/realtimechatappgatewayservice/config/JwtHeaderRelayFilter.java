package com.dipanshushukla.realtimechatappgatewayservice.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtHeaderRelayFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/ws")) {
            return chain.filter(exchange); // Skip JWT logic for WebSockets
        }

        return exchange.getPrincipal()
                .filter(principal -> principal instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> {
                    String userId = jwt.getClaimAsString("userId");
                    String username = jwt.getClaimAsString("username");

                    ServerHttpRequest mutated = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .header("X-Username", username)
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(chain.filter(exchange)); // request has no JWT
    }
}
