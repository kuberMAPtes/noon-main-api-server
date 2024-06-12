package com.kube.noon.member.service;

import reactor.core.publisher.Mono;

public interface KakaoService {

    public Mono<String> getAccessToken(String code);
    public Mono<String> getMemberInformation(String accessToken);
}
