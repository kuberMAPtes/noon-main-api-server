package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.SecurityConstants;
import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.authentication.authtoken.generator.BearerTokenAuthenticationTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Request에 담긴 Cookie에서 Access Token과 Token Type 정보를 추출해 BearerTokenAuthenticationToken에 담고
 * SecurityContext에 BearerTokenAuthenticationToken 인스턴스를 담아 AuthFilter에 전달해 인증 로직을 위임한다.
 *
 * @author PGD
 * @see BearerTokenAuthentication
 * @see BearerTokenAuthenticationTokenGenerator
 * @see AuthFilter
 * @see SecurityContextHolder
 * @see org.springframework.security.core.context.SecurityContext
 * @see org.springframework.security.core.Authentication
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final List<BearerTokenAuthenticationTokenGenerator> authTokenGeneratorList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies() == null ? new Cookie[0] : request.getCookies();
        String accessToken = getCookieValue(cookies, SecurityConstants.ACCESS_TOKEN_COOKIE_KEY.get());
        String tokenTypeStr = getCookieValue(cookies, SecurityConstants.TOKEN_TYPE_COOKIE_KEY.get());

        if (accessToken == null) {
            log.info("access token is null");
            filterChain.doFilter(request, response);
            return;
        }

        TokenType tokenType;

        try {
            tokenType = TokenType.valueOf(tokenTypeStr);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

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

        log.trace("Response Status={}", response.getStatus());
        log.trace("Set-Cookie: {}", response.getHeader("Set-Cookie"));
    }

    private String getCookieValue(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies)
                .filter((c) -> c.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }
}
