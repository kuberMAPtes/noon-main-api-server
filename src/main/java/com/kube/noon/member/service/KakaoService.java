package com.kube.noon.member.service;

import reactor.core.publisher.Mono;

public interface KakaoService {

    public Mono<String> getAccessToken(String code) throws Exception;
    public Mono<String> getUserInformation(String accessToken) throws Exception;
}
