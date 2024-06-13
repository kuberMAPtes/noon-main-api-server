package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.common.security.support.InvalidRefreshTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kube.noon.common.security.SecurityConstants.*;

/**
 * 해당 요청에 대한 Authentication, Authorization이 성공한 경우, Access Token과 Refresh Token을 새로 발급한다.
 *
 * @author PGD
 */
@Slf4j
@RequiredArgsConstructor
public class TokenRefreshFilter extends OncePerRequestFilter {
    private final List<BearerTokenSupport> tokenSupportList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, String> cookies = request.getCookies() == null
                ? new HashMap<>()
                : getCookiesOnDemand(request);
        String refreshToken = cookies.get(REFRESH_TOKEN_COOKIE_KEY.get());
        String tokenTypeStr = cookies.get(TOKEN_TYPE_COOKIE_KEY.get());
        filterChain.doFilter(request, response);

        final String refreshTokenUri = "/member/refresh";
        if (request.getRequestURI().equals(refreshTokenUri)) {
            log.trace("Request for refreshing token, so no refresh executed in this filter");
            return;
        }

        if (refreshToken == null || tokenTypeStr == null) {
            log.trace("No previous refresh token");
            return;
        }

        TokenType tokenType;
        try {
            tokenType = TokenType.valueOf(tokenTypeStr);
        } catch (IllegalArgumentException e) {
            log.warn("No such token type={}", tokenTypeStr);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            BearerTokenSupport tokenSupport = this.tokenSupportList.stream().filter((ts) -> ts.supports(tokenType)).findAny().orElseThrow();
            TokenPair tokenPair;
            try {
                tokenPair = tokenSupport.refreshToken(refreshToken);
            } catch (InvalidRefreshTokenException e) {
                log.trace("Invalid refresh token={}", request, e);
                return;
            }
            response.addCookie(wrapWithHttpOnlyCookie(ACCESS_TOKEN_COOKIE_KEY.get(), tokenPair.getAccessToken()));
            response.addCookie(wrapWithHttpOnlyCookie(REFRESH_TOKEN_COOKIE_KEY.get(), tokenPair.getRefreshToken()));
            response.addCookie(wrapWithCookie(TOKEN_TYPE_COOKIE_KEY.get(), String.valueOf(tokenType)));
            log.info("New JWT Token in Cookie");
        } else {
            log.info("Not authenticated");
        }
    }

    private Map<String, String> getCookiesOnDemand(HttpServletRequest request) {
        Map<String, String> cookies = new HashMap<>();
        Arrays.stream(request.getCookies())
                .filter((c) -> c.getName().equals(TOKEN_TYPE_COOKIE_KEY.get()) || c.getName().equals(REFRESH_TOKEN_COOKIE_KEY.get()))
                .forEach((c) -> cookies.put(c.getName(), c.getValue()));
        return cookies;
    }

    private Cookie wrapWithHttpOnlyCookie(String key, String value) {
        Cookie cookie = wrapWithCookie(key, value);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie wrapWithCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        return cookie;
    }
}
