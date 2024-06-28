package com.kube.noon.common.security.authentication.authtoken;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class GoogleTokenAuthentication extends BearerTokenAuthentication {
    private final WebAuthenticationDetails details;

    public GoogleTokenAuthentication(String token) {
        super(token);
        this.details = null;
    }

    public GoogleTokenAuthentication(String token, WebAuthenticationDetails details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
