package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthenticationToken;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Map;

/**
 * BearerToken Generator 인터페이스
 *
 * @author PGD
 * @see BearerTokenAuthenticationToken
 * @see JwtAuthenticationTokenGenerator
 * @see NoAuthenticationTokenGenerator
 * @see SimpleJsonAuthenticationTokenGenerator
 */
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
