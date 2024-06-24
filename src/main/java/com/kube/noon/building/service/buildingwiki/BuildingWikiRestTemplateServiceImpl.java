package com.kube.noon.building.service.buildingwiki;

import com.kube.noon.building.dto.wiki.BuildingWikiEditRequestDto;
import com.kube.noon.building.dto.wiki.BuildingWikiPageResponseDto;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.building.service.BuildingWikiService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;

/**
 * RestTemplate 기반 BuildingWikiService 구현체
 * 내부에서 새로운 스레드를 생성해서 요청을 보내기 때문에 비동기식으로 사용할 수 있다.
 *
 * @author PGD
 */
@Slf4j
@Service
@Profile("proddddd") // TODO: convert to "prod"
public class BuildingWikiRestTemplateServiceImpl implements BuildingWikiService {
    private static final String PAGE_TITLE_PREFIX = "Id";

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
    public void addPage(int buildingId) {
        String title = PAGE_TITLE_PREFIX + buildingId;
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

    @Transactional
    @Override
    public BuildingWikiPageResponseDto getReadPage(int buildingId) {
        URI uri = UriComponentsBuilder.fromUri(URI.create(this.buildingWikiUrl))
                .path("/wiki/index.php/" + PAGE_TITLE_PREFIX + buildingId)
                .build().toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        return new BuildingWikiPageResponseDto(
                this.buildingProfileRepository.findBuildingProfileByBuildingId(buildingId).getBuildingName(),
                responseEntity.getBody()
        );
    }

    @Override
    public BuildingWikiPageResponseDto getEditPage(int buildingId) {
        URI uri = UriComponentsBuilder.fromUri(URI.create(this.buildingWikiUrl))
                .path("/wiki/index.php")
                .queryParam("title", PAGE_TITLE_PREFIX + buildingId)
                .queryParam("action", "edit")
                .build().toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        return new BuildingWikiPageResponseDto(
                this.buildingProfileRepository.findBuildingProfileByBuildingId(buildingId).getBuildingName(),
                responseEntity.getBody()
        );
    }

    @Override
    public void editPage(int buildingId, BuildingWikiEditRequestDto dto) {
        URI uri = UriComponentsBuilder.fromUri(URI.create(this.buildingWikiUrl))
                .path("/wiki/index.php")
                .queryParam("title", PAGE_TITLE_PREFIX + buildingId)
                .queryParam("action", "submit")
                .build().toUri();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Arrays.stream(dto.getClass().getMethods())
                .filter((m) -> m.getName().startsWith("get"))
                .forEach((m) -> {
                    try {
                        Object returnValue = m.invoke(dto);
                        body.add(getFieldName(m.getName()), returnValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        RequestEntity<?> requestEntity = RequestEntity.post(uri)
                .headers(requestHeaders)
                .body(body);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        log.trace("status={}", responseEntity.getStatusCode());
    }

    private String getFieldName(String getter) {
        char[] charArray = getter.substring("get".length()).toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return String.valueOf(charArray);
    }
}
