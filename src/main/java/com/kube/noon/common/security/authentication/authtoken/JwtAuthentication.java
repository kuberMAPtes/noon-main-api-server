package com.kube.noon.common.security.authentication.authtoken;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * JWT 기반 AuthenticationToken
 *
 * @author PGD
 */
public class JwtAuthentication extends BearerTokenAuthentication {
    private final WebAuthenticationDetails details;

    public JwtAuthentication(String token) {
        super(token);
        this.details = null;
    }

    public JwtAuthentication(String token, WebAuthenticationDetails details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
