package com.kube.noon.common.security.authentication.authtoken.generator;

import com.kube.noon.common.security.authentication.authtoken.BearerTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.KakaoTokenAuthentication;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class KakaoTokenAuthenticationGenerator implements BearerTokenAuthenticationTokenGenerator {

    @Override
    public BearerTokenAuthentication generate(String token) {
        return new KakaoTokenAuthentication(token);
    }

    @Override
    public BearerTokenAuthentication generate(String token, WebAuthenticationDetails details) {
        return new KakaoTokenAuthentication(token, details);
    }

    @Override
    public boolean support(TokenType tokenType) {
        return tokenType == TokenType.KAKAO_TOKEN;
    }
}
