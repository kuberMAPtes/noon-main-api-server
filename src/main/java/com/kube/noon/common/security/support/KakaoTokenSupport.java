package com.kube.noon.common.security.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.auth.KakaoResponseDto;
import com.kube.noon.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class KakaoTokenSupport implements BearerTokenSupport {
    private static final String KAKAO_KAUTH_DOMAIN = "https://kauth.kakao.com";
    private static final String KAKAO_KAPI_DOMAIN = "https://kapi.kakao.com";
    private static final String KAKAO_LOGIN_ROUTE_PATH = "/member/kakaoLogin";
    private static final String KAKAO_OAUTH_TOKEN_PATH = "/oauth/token";
    private static final String BODY_KEY_CLIENT_ID = "client_id";
    private static final String BODY_KEY_REDIRECT_URI = "redirect_uri";
    private static final String BODY_KEY_CODE = "code";
    private static final String BODY_KEY_REFRESH_TOKEN = "refresh_token";
    private static final String BODY_KEY_GRANT_TYPE = "grant_type";

    private final WebClient webClientAuth;
    private final WebClient webClientApi;
    private final String apiKey;
    private final String mainServerHost;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoTokenSupport(@Value("${kakao.api.key}") String apiKey,
                             @Value("${main.server.host}") String mainServerHost) {
        this.apiKey = apiKey;
        this.mainServerHost = mainServerHost;
        HttpClient httpClient = HttpClient.create();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClientAuth = WebClient.builder().clientConnector(connector).baseUrl(KAKAO_KAUTH_DOMAIN).build();
        this.webClientApi = WebClient.builder().clientConnector(connector).baseUrl(KAKAO_KAPI_DOMAIN).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public TokenPair generateToken(String code) {
        final String redirectUri = this.mainServerHost + KAKAO_LOGIN_ROUTE_PATH;

        Mono<String> responseMono = this.webClientAuth.post()
                .uri(KAKAO_OAUTH_TOKEN_PATH)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData(BODY_KEY_GRANT_TYPE, "authorization_code")
                        .with(BODY_KEY_CLIENT_ID, apiKey)
                        .with(BODY_KEY_REDIRECT_URI, redirectUri)
                        .with(BODY_KEY_CODE, code))
                .exchangeToMono((response) -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class)
                                .doOnNext((body) -> log.info("응답 본문={}", body));
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                })
                .doOnError(WebClientResponseException.class, (ex) -> {
                    log.error("응답 상태 코드={}", ex.getStatusCode());
                    log.error("응답 본문={}", ex.getResponseBodyAsString());
                })
                .doOnSuccess((response) -> log.info("요청 성공"));

        return responseMono.publishOn(Schedulers.boundedElastic())
                .map((result) -> {
                    log.info("result={}", result);
                    JSONObject responseJson = new JSONObject(result);
                    String accessToken = responseJson.getString("access_token");
                    String refreshToken = responseJson.getString("refresh_token");
                    return new TokenPair(accessToken, refreshToken);
                })
                .block();
    }

    @Override
    public TokenPair refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        MultiValueMap<String, String> urlEncodedBody = new LinkedMultiValueMap<>();
        urlEncodedBody.add(BODY_KEY_GRANT_TYPE, "refresh_token");
        urlEncodedBody.add(BODY_KEY_CLIENT_ID, this.apiKey);
        urlEncodedBody.add(BODY_KEY_REFRESH_TOKEN, refreshToken);

        try {
            RequestEntity<MultiValueMap<String, String>> requestEntity =
                    RequestEntity.post(KAKAO_KAUTH_DOMAIN + KAKAO_OAUTH_TOKEN_PATH)
                            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                            .body(urlEncodedBody);
            JSONObject responseBody =
                    new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());
            log.trace("response={}", responseBody);
            String newAccessToken = responseBody.getString("access_token");
            String newRefreshToken = responseBody.has("refresh_token")
                    ? responseBody.getString("refresh_token")
                    : refreshToken;
            return new TokenPair(newAccessToken, newRefreshToken);
        } catch (HttpClientErrorException.BadRequest | JSONException e) {
            throw new InvalidRefreshTokenException("Invalid refresh token=" + refreshToken, e);
        }
    }

    public Member getMemberInformation(String accessToken) {
        final String path = "/v2/user/me";
        return this.webClientApi.get()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe((subscription) -> log.info("회원 정보 요청 구독 시작"))
                .doOnNext((res) -> log.info("회원 정보 응답 수신: {}", res))
                .doOnError((error) -> log.error("회원 정보 요청 오류", error))
                .log()
                .map((res) -> {
                    log.info("회원 정보 처리 중 - res={}", res);
                    KakaoResponseDto kakaoResponseDto;
                    try {
                        kakaoResponseDto = this.objectMapper.readValue(res, KakaoResponseDto.class);
                        log.debug("kakaoResponse={}", kakaoResponseDto);
                    } catch (JsonProcessingException e) {
                        log.error("JSON 처리 오류", e);
                        throw new RuntimeException();
                    }

                    String nickname = kakaoResponseDto.getKakaoAccount().getProfile().getNickname();
                    String email = kakaoResponseDto.getKakaoAccount().getEmail();
                    log.debug("nickname={}", nickname);
                    log.debug("email={}", email);

                    Member member = new Member();
                    member.setMemberId(email);
                    member.setNickname(nickname);
                    member.setPwd("socialLogin");
                    member.setPhoneNumber("010-0000-0000");
                    member.setProfilePhotoUrl(kakaoResponseDto.getKakaoAccount().getProfile().getProfileImage());
                    member.setProfileIntro(kakaoResponseDto.getKakaoAccount().getProfile().getProfileImage());
                    return member;
                })
                .block();
    }

    @Override
    public String extractMemberId(String token) {
        return getMemberInformation(token).getMemberId();
    }

    private static final String KAKAO_TOKEN_INFO_PATH = "/v1/user/access_token_info";

    @Override
    public boolean isTokenExpired(String token) {
        RequestEntity<Void> requestEntity = RequestEntity.get(KAKAO_KAPI_DOMAIN + KAKAO_TOKEN_INFO_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        try {
            HttpStatusCode statusCode = this.restTemplate.exchange(requestEntity, String.class).getStatusCode();
            return !statusCode.is4xxClientError();
        } catch (Exception e) {
            log.warn("Exception in sending request to {}{}", KAKAO_KAPI_DOMAIN, KAKAO_TOKEN_INFO_PATH, e);
            return false;
        }
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        // TODO: Should check what happens if sending a request with expired refresh token to the kakao auth api
        throw new UnsupportedOperationException();
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
