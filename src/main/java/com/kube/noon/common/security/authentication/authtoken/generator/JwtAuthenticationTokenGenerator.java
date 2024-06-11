package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.JwtAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * JwtAuthenticationToken을 생성하는 객체
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see BearerTokenAuthenticationToken
 * @see JwtAuthenticationToken
 */
public class JwtAuthenticationTokenGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthenticationToken generate(String token) {
        return new JwtAuthenticationToken(token);
    }

    @Override
    public BearerTokenAuthenticationToken generate(String token, WebAuthenticationDetails details) {
        return new JwtAuthenticationToken(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return tokenType == TokenType.NATIVE_TOKEN;
    }
}
