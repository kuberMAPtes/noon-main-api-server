package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.SimpleJsonAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

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
