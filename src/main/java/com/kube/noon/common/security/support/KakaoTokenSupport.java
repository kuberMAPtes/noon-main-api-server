package com.kube.noon.common.security.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.auth.KakaoResponseDto;
import com.kube.noon.member.dto.member.AddMemberDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;

@Slf4j
@Component
public class KakaoTokenSupport implements BearerTokenSupport {

    private static final String KAKAO_LOGIN_ROUTE_PATH = "/member/kakaoLogin";
    private static final String KAKAO_OAUTH_TOKEN_PATH = "/oauth/token";

    private final WebClient webClientAuth;
    private final WebClient webClientApi;
    private final String apiKey;
    private final String mainServerHost;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    public KakaoTokenSupport(@Value("${kakao.api.key}") String apiKey,
                             @Value("${main.server.host}") String mainServerHost,
                             MemberService memberService) {
        this.apiKey = apiKey;
        this.mainServerHost = mainServerHost;
        HttpClient httpClient = HttpClient.create();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClientAuth = WebClient.builder().clientConnector(connector).baseUrl("https://kauth.kakao.com").build();
        this.webClientApi = WebClient.builder().clientConnector(connector).baseUrl("https://kapi.kakao.com").build();
        this.objectMapper = new ObjectMapper();
        this.memberService = memberService;
    }

    @Override
    public TokenPair generateToken(String code) {
        final String redirectUri = this.mainServerHost + KAKAO_LOGIN_ROUTE_PATH;

        Mono<String> responseMono = this.webClientAuth.post()
                .uri(KAKAO_OAUTH_TOKEN_PATH)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", apiKey)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
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
        return null;
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

    private void addMember(String memberId, String pwd, String nickname, String phoneNumber) {
        AddMemberDto addDto = AddMemberDto.builder()
                .memberId(memberId)
                .pwd(pwd)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .socialSignUp(true)
                .build();
        this.memberService.addMember(addDto);
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
