package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.NoAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 아무런 인증도 하지 않는 AuthenticationProvider.
 * 모든 인증 요청에 대해 인가를 준다.
 *
 * @author PGD
 * @see com.kube.noon.common.security.filter.AuthFilter
 * @see NoAuthentication
 */
public class NoAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
