package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORITY_HEADER = "Authority";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorityHeader = request.getHeader(AUTHORITY_HEADER);

        if (authorityHeader == null || authorityHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String bearerToken = authorityHeader.substring(BEARER_TOKEN_PREFIX.length());

        WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
        SecurityContextHolder.getContext()
                .setAuthentication(new SimpleJsonAuthenticationToken(bearerToken, webAuthenticationDetails));
        filterChain.doFilter(request, response);
    }
}
