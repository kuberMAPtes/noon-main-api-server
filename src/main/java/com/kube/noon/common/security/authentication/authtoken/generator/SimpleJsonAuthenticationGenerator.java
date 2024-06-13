package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 개발용 BearerTokenAuthenticationTokenGenerator
 * 암호화되지 않은 JSON 기반 AuthenticationToken 생성
 *
 * @author PGD
 * @see BearerTokenAuthenticationTokenGenerator
 * @see SimpleJsonAuthentication
 */
public class SimpleJsonAuthenticationGenerator implements BearerTokenAuthenticationTokenGenerator{

    @Override
    public BearerTokenAuthentication generate(String token) {
        return new SimpleJsonAuthentication(token);
    }

    @Override
    public BearerTokenAuthentication generate(String token, WebAuthenticationDetails details) {
        return new SimpleJsonAuthentication(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return true;
    }
}
