package com.kube.noon.common.security.authentication.authtoken;

/**
 * 아무런 인증 정보도 가지지 않은 AuthenticationToken
 *
 * @author PGD
 */
public class NoAuthentication extends BearerTokenAuthentication {
    private final Object details;

    public NoAuthentication(String token) {
        super(token);
        this.details = null;
    }

    public NoAuthentication(String token, Object details) {
        super(token);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }
}
