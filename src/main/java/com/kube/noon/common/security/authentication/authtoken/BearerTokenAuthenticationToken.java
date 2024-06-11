package com.kube.noon.common.security.authentication.authtoken;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 인증에 사용되는 BearerToken을 저장하는 AuthenticationToken abstract class.
 *
 * @author PGD
 * @see JwtAuthenticationToken
 * @see NoAuthenticationToken
 * @see SimpleJsonAuthenticationToken
 */
@RequiredArgsConstructor
public abstract class BearerTokenAuthenticationToken implements Authentication {
    private final String token;

    public String getToken() {
        return this.token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract Object getDetails();

    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
}
