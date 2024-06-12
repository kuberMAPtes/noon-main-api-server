package com.kube.noon.common.security.authentication.authtoken;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class KakaoTokenAuthentication extends BearerTokenAuthentication {
    private final WebAuthenticationDetails details;

    public KakaoTokenAuthentication(String token) {
        super(token);
        this.details = null;
    }

    public KakaoTokenAuthentication(String token, WebAuthenticationDetails details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
