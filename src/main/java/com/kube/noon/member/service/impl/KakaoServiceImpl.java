package com.kube.noon.member.service.impl;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.support.KakaoTokenSupport;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.AddMemberDto;
import com.kube.noon.member.dto.member.UpdateMemberDto;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.service.KakaoService;
import com.kube.noon.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service("kakaoServiceImpl")
public class KakaoServiceImpl implements KakaoService {

    private final WebClient webClientAuth;
    private final WebClient webClientApi;
    private final KakaoTokenSupport tokenSupport;
    private final MemberService memberService;

    @Value("${kakao.api.key}")
    private String apiKey;
    @Value("${main.server.host}")
    private String mainServerHost;

    private final String KAKAO_LOGIN_ROUTE_PATH = "/member/kakaoLogin";
    // oauth/token
    @SuppressWarnings("FieldCanBeLocal")
    private final String KAKAO_OAUTH_TOKEN_PATH = "/oauth/token";



    public KakaoServiceImpl(KakaoTokenSupport kakaoTokenSupport, MemberService memberService){
        HttpClient httpClient = HttpClient.create();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClientAuth = WebClient.builder().clientConnector(connector).baseUrl("https://kauth.kakao.com").build();
        this.webClientApi = WebClient.builder().clientConnector(connector).baseUrl("https://kapi.kakao.com").build();
        this.tokenSupport = kakaoTokenSupport;
        this.memberService = memberService;
    }

    @Override
    public TokenPair generateTokenPairAndAddMemberIfNotExists(String authorizeCode){
        TokenPair tokenPair = this.tokenSupport.generateToken(authorizeCode);
        Member kakaoMember = this.tokenSupport.getMemberInformation(tokenPair.getAccessToken());
        try {
            this.memberService.findMemberById(kakaoMember.getMemberId(), kakaoMember.getMemberId());
        } catch (MemberNotFoundException e) {
            addKakaoMember(kakaoMember);
        }
        return tokenPair;
    }

    @Transactional
    public void addKakaoMember(Member infoToAdd) {
        AddMemberDto addDto = new AddMemberDto();
        BeanUtils.copyProperties(infoToAdd, addDto);
        addDto.setSocialSignUp(true);
        this.memberService.addMember(addDto);
        this.memberService.findMemberById(addDto.getMemberId())
                .ifPresent((found) -> {
                    UpdateMemberDto updateMemberDto = new UpdateMemberDto();
                    BeanUtils.copyProperties(infoToAdd, updateMemberDto);
                    log.info("update to={}", updateMemberDto);
                    this.memberService.updateMember(updateMemberDto);
                });
    }

    @Override
    public Mono<String> getMemberInformation(String accessToken){
        System.out.println("getMemberInformation() 호출 :: 카카오서비스");
        String path = "/v2/user/me";
        System.out.println(path);

        return webClientApi.get()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class);
    }

}
