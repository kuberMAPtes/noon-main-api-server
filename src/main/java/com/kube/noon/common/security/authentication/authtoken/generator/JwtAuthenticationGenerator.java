package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.JwtAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * JwtAuthenticationToken을 생성하는 객체
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see BearerTokenAuthentication
 * @see JwtAuthentication
 */
public class JwtAuthenticationGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthentication generate(String token) {
        return new JwtAuthentication(token);
    }

    @Override
    public BearerTokenAuthentication generate(String token, WebAuthenticationDetails details) {
        return new JwtAuthentication(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return tokenType == TokenType.NATIVE_TOKEN;
    }
}
