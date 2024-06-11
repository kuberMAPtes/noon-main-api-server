package com.kube.noon.common.security.authentication.authtoken;

/**
 * 아무런 인증 정보도 가지지 않은 AuthenticationToken
 *
 * @author PGD
 */
public class NoAuthenticationToken extends BearerTokenAuthenticationToken {
    private final Object details;

    public NoAuthenticationToken(String token) {
        super(token);
        this.details = null;
    }

    public NoAuthenticationToken(String token, Object details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
