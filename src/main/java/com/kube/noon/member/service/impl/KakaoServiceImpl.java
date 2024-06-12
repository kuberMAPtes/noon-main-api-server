package com.kube.noon.member.service.impl;

import com.kube.noon.member.service.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


@Service("kakaoServiceImpl")
public class KakaoServiceImpl implements KakaoService {

    private final WebClient webClientAuth;
    private final WebClient webClientApi;

    @Value("${kakao.api.key}")
    private String apiKey;
    @Value("${main.server.host}")
    private String mainServerHost;

    private final String KAKAO_LOGIN_ROUTE_PATH = "/member/kakaoLogin";
    // oauth/token
    @SuppressWarnings("FieldCanBeLocal")
    private final String KAKAO_OAUTH_TOKEN_PATH = "/oauth/token";



    public KakaoServiceImpl(){
        HttpClient httpClient = HttpClient.create();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClientAuth = WebClient.builder().clientConnector(connector).baseUrl("https://kauth.kakao.com").build();
        this.webClientApi = WebClient.builder().clientConnector(connector).baseUrl("https://kapi.kakao.com").build();
    }


    @Override
    public Mono<String> getAccessToken(String authorize_code){
        System.out.println("getAccessToken() 호출 :: 카카오서비스");
        System.out.println("authorize_code: "+authorize_code);
        String REDIRECT_URI = this.mainServerHost + KAKAO_LOGIN_ROUTE_PATH;
        System.out.println("redirect_uri: "+REDIRECT_URI);
        /*
        -H는 헤더 -d는 바디
        * curl -v -X POST "https://kauth.kakao.com/oauth/token" \
                 -H "Content-Type: application/x-www-form-urlencoded" \
                 -d "grant_type=authorization_code" \
                 -d "client_id=${REST_API_KEY}" \
                 --data-urlencode "redirect_uri=${REDIRECT_URI}" \
                 -d "code=${AUTHORIZE_CODE}"
        *라고 webClient가 보내야 해.
        * */
        return webClientAuth.post()
                .uri(KAKAO_OAUTH_TOKEN_PATH)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", apiKey)
                        .with("redirect_uri", REDIRECT_URI)
                        .with("code", authorize_code))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class)
                                .doOnNext(body -> System.out.println("응답 본문: " + body));
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    System.err.println("응답 상태 코드: " + ex.getStatusCode());
                    System.err.println("응답 본문: " + ex.getResponseBodyAsString());
                })
                .doOnSuccess(response -> System.out.println("요청 성공"));


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
