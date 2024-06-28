package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.GoogleTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class GoogleTokenAuthenticationGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthentication generate(String token) {
        return new GoogleTokenAuthentication(token);
    }

    @Override
    public BearerTokenAuthentication generate(String token, WebAuthenticationDetails details) {
        return new GoogleTokenAuthentication(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return tokenType == TokenType.GOOGLE_TOKEN;
    }
}
