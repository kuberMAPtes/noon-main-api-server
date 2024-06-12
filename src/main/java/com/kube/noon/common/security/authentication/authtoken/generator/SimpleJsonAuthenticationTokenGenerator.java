package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 개발용 BearerTokenAuthenticationTokenGenerator
 * 암호화되지 않은 JSON 기반 AuthenticationToken 생성
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see SimpleJsonAuthenticationToken
 */
public class SimpleJsonAuthenticationTokenGenerator implements BearerTokenAuthenticationTokenGenerator{

    @Override
    public BearerTokenAuthenticationToken generate(String token) {
        return new SimpleJsonAuthenticationToken(token);
    }

    @Override
    public BearerTokenAuthenticationToken generate(String token, WebAuthenticationDetails details) {
        return new SimpleJsonAuthenticationToken(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return true;
    }
}
