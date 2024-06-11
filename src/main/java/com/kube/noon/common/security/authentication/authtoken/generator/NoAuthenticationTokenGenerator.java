package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.NoAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 어떤 Authenticaiton, Authorization도 필요하지 않을 때 사용하는 BearerTokenAuthenticationTokenGenerator
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see NoAuthenticationToken
 */
public class NoAuthenticationTokenGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthenticationToken generate(String token) {
        return new NoAuthenticationToken("");
    }

    @Override
    public BearerTokenAuthenticationToken generate(String token, WebAuthenticationDetails details) {
        return new NoAuthenticationToken("", details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return true;
    }
}
