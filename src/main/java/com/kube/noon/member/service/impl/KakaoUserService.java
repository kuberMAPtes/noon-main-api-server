package com.kube.noon.member.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kube.noon.member.dto.kakao.KakaoResponse;
import com.kube.noon.member.dto.member.AddMemberDto;
import com.kube.noon.member.dto.util.RandomData;
import com.kube.noon.member.service.KakaoService;
import com.kube.noon.member.service.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
public class KakaoUserService {

    @Autowired
    private KakaoService kakaoService;

    @Autowired
    private MemberService memberService;

    @Transactional
    public Mono<Void> handleKakaoLogin(String authorizeCode) throws Exception {
        return kakaoService.getAccessToken(authorizeCode)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(result -> {
                    JSONObject resultJsonObject = new JSONObject(result);
                    String accessToken = resultJsonObject.getString("access_token");

                    try {
                        return kakaoService.getMemberInformation(accessToken)
                                .map(response -> {
                                    KakaoResponse kakaoResponse;
                                    try {
                                        kakaoResponse = new ObjectMapper().readValue(response, KakaoResponse.class);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    String nickname = kakaoResponse.getKakao_account().getProfile().getNickname();
                                    String id = kakaoResponse.getId();

                                    AddMemberDto newMember = new AddMemberDto();
                                    newMember.setMemberId(id);
                                    newMember.setNickname(nickname);
                                    newMember.setPwd("socialLogin");
                                    newMember.setPhoneNumber(RandomData.getRandomPhoneNumber());

                                    Optional.ofNullable(memberService.findMemberById(id))
                                            .ifPresentOrElse(user -> {}, () -> memberService.addMember(newMember));

                                    return id;
                                });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).then();
    }
}
