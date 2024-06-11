package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenRefreshFilter extends OncePerRequestFilter {
    public static final String ACCESS_TOKEN_COOKIE_KEY = "token";
    public static final String REFRESH_TOKEN_COOKIE_KEY = "refresh_token";
    public static final String TOKEN_TYPE_COOKIE_KEY = "token_type";

    private final BearerTokenSupport tokenSupport;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof UsernamePasswordAuthenticationToken) {
            String memberId = (String)authentication.getPrincipal();
            String accessToken = this.tokenSupport.generateAccessToken(memberId);
            String refreshToken = this.tokenSupport.generateRefreshToken(memberId);
            response.addCookie(new Cookie(ACCESS_TOKEN_COOKIE_KEY, accessToken));
            response.addCookie(new Cookie(REFRESH_TOKEN_COOKIE_KEY, refreshToken));
            response.addCookie(new Cookie(TOKEN_TYPE_COOKIE_KEY, String.valueOf(TokenType.NATIVE_TOKEN.getCode())));
            log.info("New JWT Token in Cookie");
        } else {
            log.info("Not authenticated");
        }
    }
}
