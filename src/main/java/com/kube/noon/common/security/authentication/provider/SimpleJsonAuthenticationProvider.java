package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 암호화하지 않은 JSON 토큰 기반 인증 로직을 수행하는 AuthenticationProvider.
 *
 * @author PGD
 * @see SimpleJsonAuthenticationToken
 * @see com.kube.noon.common.security.filter.AuthFilter
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleJsonAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SimpleJsonAuthenticationToken simpleJson = (SimpleJsonAuthenticationToken)authentication;

        JSONObject token = new JSONObject(simpleJson.getToken());
        String memberId = token.getString("memberId");
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(memberId);

            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("authentication class={}", authentication);
        return authentication == SimpleJsonAuthenticationToken.class;
    }
}
