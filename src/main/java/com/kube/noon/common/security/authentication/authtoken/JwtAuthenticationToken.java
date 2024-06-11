package com.kube.noon.common.security.authentication.authtoken;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class JwtAuthenticationToken extends BearerTokenAuthenticationToken {
    private final WebAuthenticationDetails details;

    public JwtAuthenticationToken(String token) {
        super(token);
        this.details = null;
    }

    public JwtAuthenticationToken(String token, WebAuthenticationDetails details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
