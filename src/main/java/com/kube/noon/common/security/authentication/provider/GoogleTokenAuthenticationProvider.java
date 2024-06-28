package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.GoogleTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.KakaoTokenAuthentication;
import com.kube.noon.common.security.support.GoogleTokenSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class GoogleTokenAuthenticationProvider implements AuthenticationProvider {
    private final GoogleTokenSupport support;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = ((GoogleTokenAuthentication)authentication).getToken();
        String memberId = this.support.extractMemberId(token);
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(memberId);
            if (this.support.isTokenExpired(token)) {
                log.trace("token has been expired");
                return UsernamePasswordAuthenticationToken.unauthenticated("", "");
            }
            log.trace("{} is authenticated", memberId);
            return UsernamePasswordAuthenticationToken.authenticated(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            log.warn("No such user {}", memberId, e);
            return UsernamePasswordAuthenticationToken.unauthenticated("", "");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == GoogleTokenAuthentication.class;
    }
}
