package com.kube.noon.common.security.authentication.authtoken;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 암호화되지 않은 JSON 토큰을 가진 AuthenticationToken
 *
 * @author PGD
 */
public class SimpleJsonAuthenticationToken extends BearerTokenAuthenticationToken {
    private WebAuthenticationDetails details;

    public SimpleJsonAuthenticationToken(String token) {
        super(token);
    }

    public SimpleJsonAuthenticationToken(String token, WebAuthenticationDetails details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
