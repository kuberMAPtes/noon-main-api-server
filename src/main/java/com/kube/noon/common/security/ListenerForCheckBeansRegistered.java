package com.kube.noon.common.security;

import com.kube.noon.common.security.authentication.authtoken.generator.BearerTokenAuthenticationTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListenerForCheckBeansRegistered {
    private final ApplicationContext applicationContext;
    private final ProviderManager authenticationManager;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        print(this.applicationContext.getBeansOfType(SecurityFilterChain.class));
        print(this.applicationContext.getBeansOfType(BearerTokenAuthenticationTokenGenerator.class));
        print(this.applicationContext.getBeansOfType(AuthenticationManager.class));
        authenticationManager.getProviders()
                .forEach((p) -> log.trace("provider={}", p));
    }

    private void print(Map<String, ?> map) {
        map.forEach((k, v) ->
                log.trace("{}={}", k, v));
    }
}
