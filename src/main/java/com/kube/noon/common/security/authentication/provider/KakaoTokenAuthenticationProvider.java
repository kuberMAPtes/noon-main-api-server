package com.kube.noon.common.security.authentication.provider;

import com.kube.noon.common.security.authentication.authtoken.KakaoTokenAuthentication;
import com.kube.noon.common.security.support.KakaoTokenSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.RequestEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Kakao 토큰 인증 기반 AuthenticationProvider
 * AuthFilter에서 인증이 이루어진다.
 *
 * @author PGD
 * @see com.kube.noon.common.security.filter.AuthFilter
 * @see KakaoTokenAuthentication
 * @see KakaoTokenSupport
 * @see JwtAuthenticationProvider
 */
@Slf4j
@RequiredArgsConstructor
public class KakaoTokenAuthenticationProvider implements AuthenticationProvider {
    private static final String API_URI = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO: RestTemplate을 WebClient로 리팩토링
        String kakaoAccessToken = ((KakaoTokenAuthentication)authentication).getToken();
        MultiValueMap<String, String> urlEncodedBody = new LinkedMultiValueMap<>();
        urlEncodedBody.add("property_keys", "[\"kakao_account.email\"]");
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(API_URI)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .body(urlEncodedBody);

        JSONObject responseBody;
        try {
            responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());
            log.trace("responseBody={}", responseBody);
        } catch (RestClientException e) {
            log.error("Error", e);
            return UsernamePasswordAuthenticationToken.unauthenticated("", "");
        }

        String memberId = responseBody.getJSONObject("kakao_account").getString("email");

        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(memberId);
            return UsernamePasswordAuthenticationToken.authenticated(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            log.error("memberId of \"{}\" is not found", memberId, e);
            return UsernamePasswordAuthenticationToken.unauthenticated("", "");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == KakaoTokenAuthentication.class;
    }
}
