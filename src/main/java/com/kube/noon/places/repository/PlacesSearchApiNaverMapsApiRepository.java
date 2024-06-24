package com.kube.noon.places.repository;

import com.kube.noon.places.domain.Place;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Repository
@Primary
public class PlacesSearchApiNaverMapsApiRepository extends PlacesNaverMapsApiRepositoryImpl {
    private static final int DISPLAY_NUMBER = 5;
    private static final String NAVER_PLACE_SEARCH_API_BASE_URL = "https://openapi.naver.com";

    private final String clientId;
    private final String clientSecret;

    public PlacesSearchApiNaverMapsApiRepository(
            @Value("${geocode.naver.access-key}") String accessKey,
            @Value("${geocode.naver.secret-key}") String secretKey,
            @Value("${place-search.naver.client-id}") String clientId,
            @Value("${place-search.naver.client-secret}") String clientSecret
    ) {
        super(accessKey, secretKey);
        this.clientId = clientId;
        this. clientSecret = clientSecret;
    }

    @Override
    public List<Place> findByPlaceName(String placeName) {
        try {
            URI uri = UriComponentsBuilder.fromUri(URI.create(NAVER_PLACE_SEARCH_API_BASE_URL))
                    .path("/v1/search/local.json")
                    .queryParam("display", DISPLAY_NUMBER)
                    .queryParam("query", placeName)
                    .encode(StandardCharsets.UTF_8)
                    .encode()
                    .build()
                    .toUri();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("X-Naver-Client-Id", this.clientId);
            requestHeaders.add("X-Naver-Client-Secret", this.clientSecret);
            requestHeaders.add(HttpHeaders.ACCEPT, "*/*");
            RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(requestHeaders).build();

            log.debug("uri={}", uri);

            String body = this.restTemplate.exchange(requestEntity, String.class).getBody();
            log.debug("body={}", body);

            JSONObject jsonObject = new JSONObject(body);
            log.debug("jsonObject={}", jsonObject);
            JSONArray items = jsonObject.getJSONArray("items");
            log.debug("items={}", items);
            List<Place> resultPlaces = new LinkedList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.getString("title");
                String roadAddress = item.getString("roadAddress");
                Place place = super.findByPlaceName(roadAddress).get(0);
                resultPlaces.add(Place.builder()
                        .placeName(title)
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .roadAddress(roadAddress)
                        .build());
            }
            log.debug("resultPlaces={}", resultPlaces);
            return resultPlaces;
        } catch (InvalidDataAccessResourceUsageException e) {
            throw new InvalidDataAccessResourceUsageException("URI syntax error for Naver Search API", e);
        } catch (Exception e) {
            log.error("error", e);
            return List.of();
        }
    }
}
