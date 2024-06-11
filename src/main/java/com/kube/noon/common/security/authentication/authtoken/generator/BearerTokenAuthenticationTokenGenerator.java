package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Map;

public interface BearerTokenAuthenticationTokenGenerator {
    Map<TokenType, BearerTokenAuthenticationTokenGenerator> instances = Map.of(
            TokenType.NATIVE_TOKEN, new JwtAuthenticationTokenGenerator()
    );

    static BearerTokenAuthenticationTokenGenerator getInstance(TokenType tokenType) {
        return instances.get(tokenType);
    }

    BearerTokenAuthenticationToken generate(String token);

    BearerTokenAuthenticationToken generate(String token, WebAuthenticationDetails details);

    boolean support(TokenType tokenType);
}
