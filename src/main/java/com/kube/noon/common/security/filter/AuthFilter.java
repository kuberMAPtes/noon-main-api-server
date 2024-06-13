package com.kube.noon.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TokenAuthenticationFilter에서 SecurityContext에 세팅한 Authentication 정보를 기반으로 사용자 인증 로직을 수행하는 Filter.
 * 인증 로직은 AuthenticationManager를 통해 수행한다.
 *
 * @author PGD
 * @see TokenAuthenticationFilter
 * @see AuthenticationManager
 * @see org.springframework.security.authentication.ProviderManager
 * @see org.springframework.security.authentication.AuthenticationProvider
 * @see com.kube.noon.common.security.authentication.provider.JwtAuthenticationProvider
 * @see com.kube.noon.common.security.authentication.provider.NoAuthenticationProvider
 * @see com.kube.noon.common.security.authentication.provider.SimpleJsonAuthenticationProvider
 */
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.trace("authentication={}", authentication);

        if (authentication == null) {
            log.debug("authentication is null");
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);
        filterChain.doFilter(request, response);
    }
}
