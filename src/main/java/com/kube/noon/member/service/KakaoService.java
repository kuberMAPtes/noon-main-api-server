package com.kube.noon.member.service;

import com.kube.noon.common.security.TokenPair;
import reactor.core.publisher.Mono;

public interface KakaoService {

    public TokenPair generateTokenPairAndAddMemberIfNotExists(String code);
    public Mono<String> getMemberInformation(String accessToken);
}
