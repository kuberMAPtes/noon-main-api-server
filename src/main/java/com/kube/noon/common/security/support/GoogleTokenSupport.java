package com.kube.noon.common.security.support;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import org.json.JSONObject;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
public class GoogleTokenSupport implements BearerTokenSupport {

    @Override
    public TokenPair generateToken(String code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenPair refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        throw new InvalidRefreshTokenException("Google login doesn't support refreshing token");
    }

    @Override
    public String extractMemberId(String token) {
        RequestEntity<Void> requestEntity =
                RequestEntity.post(UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                                .queryParam("id_token", token)
                                .encode(StandardCharsets.UTF_8)
                                .toUriString())
                        .build();
        JSONObject responseBody = new JSONObject(new RestTemplate().exchange(requestEntity, String.class).getBody());
        return responseBody.getString("email");
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            RequestEntity<Void> requestEntity =
                    RequestEntity.post(UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                                    .queryParam("id_token", token)
                                    .encode(StandardCharsets.UTF_8)
                                    .toUriString())
                            .build();
            return false;
        } catch (Exception e) {
            return true;
        }
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
        return tokenType == TokenType.GOOGLE_TOKEN;
    }
}
