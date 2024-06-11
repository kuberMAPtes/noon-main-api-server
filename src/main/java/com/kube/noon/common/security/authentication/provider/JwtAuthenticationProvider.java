package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.JwtAuthenticationToken;
import com.kube.noon.common.security.support.BearerTokenSupport;
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
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final BearerTokenSupport tokenSupport;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = ((JwtAuthenticationToken) authentication).getToken();

        if (this.tokenSupport.isTokenExpired(jwtToken) || this.tokenSupport.isRefreshToken(jwtToken)) {
            return null;
        }

        String memberId = this.tokenSupport.extractMemberId(jwtToken);
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(memberId);
            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            log.debug("memberId of \"{}\" is not found", memberId, e);
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("authentication={}", authentication);
        return authentication == JwtAuthenticationToken.class;
    }
}
