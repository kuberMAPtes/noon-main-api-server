package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.authentication.authtoken.JwtAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.authentication.authtoken.generator.BearerTokenAuthenticationTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final List<BearerTokenAuthenticationTokenGenerator> authTokenGeneratorList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies() == null ? new Cookie[0] : request.getCookies();
        String accessToken = getCookieValue(cookies, TokenRefreshFilter.ACCESS_TOKEN_COOKIE_KEY);
        String tokenTypeStr = getCookieValue(cookies, TokenRefreshFilter.TOKEN_TYPE_COOKIE_KEY);

        if (accessToken == null) {
            log.info("access token is null");
            filterChain.doFilter(request, response);
            return;
        }

        TokenType tokenType = tokenTypeStr == null ? TokenType.NATIVE_TOKEN : TokenType.valueOf(tokenTypeStr);

        WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);

        List<BearerTokenAuthenticationTokenGenerator> supports =
                this.authTokenGeneratorList.stream().filter((g) -> g.support(tokenType)).toList();

        if (supports.size() > 1) {
            throw new RuntimeException("1개 초과한 TokenGenerator: " + supports);
        } else if (supports.isEmpty()) {
            throw new RuntimeException("지원되지 않는 Token Type: " + tokenType); // TODO: Specific Exception
        }

        BearerTokenAuthenticationTokenGenerator support = supports.get(0);

        log.info("token type={}", tokenType);
        log.info("support={}", support);

        SecurityContextHolder.getContext()
                .setAuthentication(support.generate(accessToken, webAuthenticationDetails));
        filterChain.doFilter(request, response);
    }

    private String getCookieValue(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies)
                .filter((c) -> c.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }
}
