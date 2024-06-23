package com.kube.noon.building.service.buildingwiki;

import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.building.service.BuildingWikiService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * RestTemplate 기반 BuildingWikiService 구현체
 * 내부에서 새로운 스레드를 생성해서 요청을 보내기 때문에 비동기식으로 사용할 수 있다.
 *
 * @author PGD
 */
@Slf4j
@Service
@Profile("prod")
public class BuildingWikiRestTemplateServiceImpl implements BuildingWikiService {
    private final BuildingProfileRepository buildingProfileRepository;
    private final String buildingWikiUrl;
    private final RestTemplate restTemplate;

    public BuildingWikiRestTemplateServiceImpl(BuildingProfileRepository buildingProfileRepository,
                                               @Value("${building-wiki-url}") String buildingWikiUrl) {
        this.buildingProfileRepository = buildingProfileRepository;
        this.buildingWikiUrl = buildingWikiUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void addPage(String title) {
        if (this.buildingWikiUrl == null) {
            throw new IllegalStateException(this.getClass() + ".buildingWikiUrl is null");
        }

        new Thread(() -> {
            if (isExist(title)) {
                log.info("Building Wiki Page of title({}) already exists", title);
                return;
            }

            log.info("Create Wiki Page of title: {}", title);
            URI uri = UriComponentsBuilder.fromUri(URI.create(this.buildingWikiUrl))
                    .path("/wiki/api.php")
                    .queryParam("action", "edit")
                    .queryParam("title", title)
                    .queryParam("text", "")
                    .queryParam("format", "json")
                    .build().toUri();
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("token", "+\\");
            RequestEntity<MultiValueMap<String, String>> requestEntity =
                    RequestEntity.post(uri)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(body);
            this.restTemplate.exchange(requestEntity, String.class);
        }).start();
    }

    private boolean isExist(String title) {
        URI uri = UriComponentsBuilder.fromUri(URI.create(this.buildingWikiUrl))
                .path("/wiki/api.php")
                .queryParam("action", "query")
                .queryParam("titles", title)
                .queryParam("format", "json")
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        JSONObject body = new JSONObject(responseEntity.getBody());
        JSONObject pages = body.getJSONObject("query").getJSONObject("pages");
        try {
            pages.getJSONObject("-1");
            return false;
        } catch (JSONException e) {
            return true;
        }
    }

    @Override
    public String getPageInHtml(int buildingId) {

        return null;
    }
}
