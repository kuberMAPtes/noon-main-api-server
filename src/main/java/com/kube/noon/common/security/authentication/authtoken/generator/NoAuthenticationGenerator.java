package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.NoAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 어떤 Authenticaiton, Authorization도 필요하지 않을 때 사용하는 BearerTokenAuthenticationTokenGenerator
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see NoAuthentication
 */
public class NoAuthenticationGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthentication generate(String token) {
        return new NoAuthentication("");
    }

    @Override
    public BearerTokenAuthentication generate(String token, WebAuthenticationDetails details) {
        return new NoAuthentication("", details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return true;
    }
}
