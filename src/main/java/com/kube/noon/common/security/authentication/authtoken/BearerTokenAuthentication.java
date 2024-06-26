package com.kube.noon.common.security.authentication.authtoken;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 인증에 사용되는 BearerToken을 저장하는 AuthenticationToken abstract class.
 *
 * @author PGD
 * @see JwtAuthentication
 * @see NoAuthentication
 * @see SimpleJsonAuthentication
 */
@RequiredArgsConstructor
public abstract class BearerTokenAuthentication implements Authentication {
    private final String token;
    private boolean authenticated = false;

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
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
}
