package com.kube.noon.common.messagesender;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ApickApiAgentImpl implements ApickApiAgent {

    private final WebClient webClient;

    private final String authKey;

    private final ObjectMapper objectMapper;

    public ApickApiAgentImpl(WebClient.Builder webClientBuilder
            , @Value("${cl.auth.key}") String authKey
            , ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://apick.app").build();
        this.authKey = authKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {

        String response = webClient.post()
                .uri("/rest/check_phone_valid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("CL_AUTH_KEY", authKey)
                .body(BodyInserters.fromFormData("number", phoneNumber))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(log::info)
                .block();

        try{
            JsonNode root = objectMapper.readTree(response);
            boolean validity = root.path("data").path("valid").asBoolean();
            return validity;
        }catch( JsonProcessingException e){
            log.error("JsonProcessingException", e);
            return false;
        }

    }
}
