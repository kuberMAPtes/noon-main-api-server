package com.kube.noon.common.security.authentication.authtoken;

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
