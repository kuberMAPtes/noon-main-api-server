package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Map;

/**
 * BearerToken Generator 인터페이스
 *
 * @author PGD
 * @see BearerTokenAuthentication
 * @see JwtAuthenticationGenerator
 * @see NoAuthenticationGenerator
 * @see SimpleJsonAuthenticationGenerator
 */
public interface BearerTokenAuthenticationTokenGenerator {
    Map<TokenType, BearerTokenAuthenticationTokenGenerator> instances = Map.of(
            TokenType.NATIVE_TOKEN, new JwtAuthenticationGenerator()
    );

    static BearerTokenAuthenticationTokenGenerator getInstance(TokenType tokenType) {
        return instances.get(tokenType);
    }

    BearerTokenAuthentication generate(String token);

    BearerTokenAuthentication generate(String token, WebAuthenticationDetails details);

    boolean support(TokenType tokenType);
}
