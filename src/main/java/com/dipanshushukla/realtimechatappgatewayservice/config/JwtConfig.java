package com.dipanshushukla.realtimechatappgatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

import java.util.List;

@Configuration
public class JwtConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        List<ServiceInstance> instances = discoveryClient.getInstances("realtime-chatapp-auth-service");
        if (instances.isEmpty()) {
            throw new IllegalStateException("Authentication service instance not found");
        }

        String jwkSetUri = instances.get(0).getUri().toString() + "/.well-known/jwks.json";
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}