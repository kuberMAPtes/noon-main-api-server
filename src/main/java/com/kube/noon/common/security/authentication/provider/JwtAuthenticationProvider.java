package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.JwtAuthentication;
import com.kube.noon.common.security.support.BearerTokenSupport;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * JWT 기반 인증 로직을 수행하는 AuthenticationProvider
 * AuthFilter에서 인증이 이루어진다.
 * JWT 토큰을 파싱하여 회원 ID를 가져오고, 회원 ID를 통해 DB에서 회원 정보를 가져와
 * UsernamePasswordAuthenticationToken에 담는다.
 *
 * @author PGD
 * @see com.kube.noon.common.security.filter.AuthFilter
 * @see JwtAuthentication
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final BearerTokenSupport tokenSupport;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = ((JwtAuthentication)authentication).getToken();

        log.trace("jwtToken={}", jwtToken);

        try {
            if (this.tokenSupport.isTokenExpired(jwtToken) || this.tokenSupport.isRefreshToken(jwtToken)) {
                log.trace("token is not good");
                log.trace("isTokenExpired={}", this.tokenSupport.isTokenExpired(jwtToken));
                log.trace("isRefreshToken={}", this.tokenSupport.isRefreshToken(jwtToken));
                return UsernamePasswordAuthenticationToken.unauthenticated("", "");
            }
        } catch (JwtException e) {
            log.trace("JwtException", e);
            return UsernamePasswordAuthenticationToken.unauthenticated("", "");
        }

        String memberId = this.tokenSupport.extractMemberId(jwtToken);
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(memberId);
            return UsernamePasswordAuthenticationToken.authenticated(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            log.debug("memberId of \"{}\" is not found", memberId, e);
            return UsernamePasswordAuthenticationToken.unauthenticated("", "");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.trace("authentication={}", authentication);
        log.trace("authentication == JwtAuthentication.class={}", authentication == JwtAuthentication.class);
        return authentication == JwtAuthentication.class;
    }
}
