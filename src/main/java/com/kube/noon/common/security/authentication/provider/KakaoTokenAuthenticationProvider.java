package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.KakaoTokenAuthentication;
import com.kube.noon.member.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class KakaoTokenAuthenticationProvider implements AuthenticationProvider {
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO: RestTemplate을 WebClient로 리팩토링
        String kakaoToken = ((KakaoTokenAuthentication)authentication).getToken();


        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == KakaoTokenAuthentication.class;
    }
}
