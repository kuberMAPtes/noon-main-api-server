package com.kube.noon.common.security.support;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoTokenSupport implements BearerTokenSupport {

    private static final String KAKAO_LOGIN_ROUTE_PATH = "/member/kakaoLogin";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${main.server.host}")
    private String mainServerHost;

    @Override
    public TokenPair generateToken(String memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenPair refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        return null;
    }

    @Override
    public String extractMemberId(String token) {
        return null;
    }

    @Override
    public boolean isTokenExpired(String token) {
        return false;
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        return false;
    }

    @Override
    public boolean isRefreshToken(String token) {
        return false;
    }

    @Override
    public void invalidateRefreshToken(String refreshToken) {

    }

    @Override
    public void invalidateRefreshTokenByMemberId(String memberId) {

    }

    @Override
    public boolean supports(TokenType tokenType) {
        return tokenType == TokenType.KAKAO_TOKEN;
    }
}
