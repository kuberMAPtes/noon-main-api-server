/**
 * 네이버 Geocode, Reverse Geocode API에서 데이터를 가져오는 Repsoitory
 *
 * @author 박경도
 */

package com.kube.noon.places.repository;

import com.kube.noon.places.domain.Place;
import com.kube.noon.places.exception.PlaceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Repository
public class PlacesNaverMapsApiRepositoryImpl implements PlacesRepository {
    private static final String GEOCODE_ACCESS_KEY_HEADER = "X-NCP-APIGW-API-KEY-ID";
    private static final String GEOCODE_SECRET_KEY_HEADER = "X-NCP-APIGW-API-KEY";
    private static final String NAVER_CLOUD_GEOCODE_API_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";
    private static final String NAVER_CLOUD_REVERSE_GEOCODE_API_URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc";
    private static final String QUERY_PARAMETER_KEY_QUERY = "query";
    private static final String QUERY_PARAMETER_KEY_COORDS = "coords";
    private static final String QUERY_PARAMETER_KEY_OUTPUT = "output";
    private static final String QUERY_PARAMETER_KEY_ORDERS = "orders";
    private static final String RESPONSE_TYPE = "json";
    private static final String ORDERS = "roadaddr";
    private static final String LAT_LNG_DELIMITER = ",";
    private static final int DISPLAY_NUMBER = 5;
    private static final String NAVER_PLACE_SEARCH_API_BASE_URL = "https://openapi.naver.com";

    private final String accessKey;
    private final String secretKey;
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public PlacesNaverMapsApiRepositoryImpl(
            @Value("${geocode.naver.access-key}") String accessKey,
            @Value("${geocode.naver.secret-key}") String secretKey,
            @Value("${place-search.naver.client-id}") String clientId,
            @Value("${place-search.naver.client-secret}") String clientSecret
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.restTemplate = new RestTemplate();
        this.clientId = clientId;
        this. clientSecret = clientSecret;
    }

    @Override
    public List<Place> searchPlaceList(String searchKeyword) {
        try {
            URI uri = UriComponentsBuilder.fromUri(URI.create(NAVER_PLACE_SEARCH_API_BASE_URL))
                    .path("/v1/search/local.json")
                    .queryParam("display", DISPLAY_NUMBER)
                    .queryParam("query", searchKeyword)
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
                Place place = findByPlaceName(roadAddress).get(0);
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

    @Override
    public List<Place> findByPlaceName(String placeName) {
        try {

            String url = UriComponentsBuilder.fromHttpUrl(NAVER_CLOUD_GEOCODE_API_URL)
                    .queryParam(QUERY_PARAMETER_KEY_QUERY, placeName).encode().toUriString();
            log.debug("request url={}", url);

            RequestEntity<Void> requestEntity = RequestEntity.get(new URI(url))
                    .headers(getCommonHeaders()).build();

            ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            JSONArray addresses = jsonObject.getJSONArray("addresses");
            List<Place> places = new ArrayList<>();
            for (int i = 0; i < addresses.length(); i++) {
                JSONObject jsonObj = (JSONObject) addresses.get(i);
                places.add(Place.builder()
                        .roadAddress(jsonObj.getString("roadAddress"))
                        .latitude(Double.parseDouble(jsonObj.getString("y")))
                        .longitude(Double.parseDouble(jsonObj.getString("x")))
                        .placeName(placeName)
                        .build());
            }
            return places;
        } catch (URISyntaxException e) {
            throw new InvalidDataAccessResourceUsageException("URI syntax error for Naver Maps", e); // TODO
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Place findByPosition(double latitude, double longitude) throws PlaceNotFoundException {
        try {
            HttpHeaders headers = getCommonHeaders();

            String url = UriComponentsBuilder.fromHttpUrl(NAVER_CLOUD_REVERSE_GEOCODE_API_URL)
                    .queryParam(QUERY_PARAMETER_KEY_COORDS, String.format("%f%s%f", longitude, LAT_LNG_DELIMITER, latitude))
                    .queryParam(QUERY_PARAMETER_KEY_OUTPUT, RESPONSE_TYPE)
                    .queryParam(QUERY_PARAMETER_KEY_ORDERS, ORDERS)
                    .encode().toUriString();
            log.debug("request url={}", url);

            RequestEntity<Void> requestEntity = RequestEntity.get(new URI(url))
                    .headers(headers).build();

            ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
            JSONObject root = new JSONObject(responseEntity.getBody());
            System.out.println(root);
            JSONArray results = root.getJSONArray("results");
            if (results.length() < 1) {
                throw new PlaceNotFoundException(String.format("다음 위치 좌표에 해당하는 건물이 없습니다=%f,%f", latitude, longitude));
            }
            JSONObject result = results.getJSONObject(0);
            JSONObject land = result.getJSONObject("land");
            String name = land.getString("name");
            String number = land.getString("number1");
            log.trace("land={}", land);
            Place byRoadAddr = findByPlaceName(name + " " + number).get(0);
            return Place.builder()
                    .roadAddress(byRoadAddr.getRoadAddress())
                    .latitude(byRoadAddr.getLatitude())
                    .longitude(byRoadAddr.getLongitude())
                    .placeName(land.getJSONObject("addition0").getString("value"))
                    .build();
        } catch (URISyntaxException e) {
            throw new InvalidDataAccessResourceUsageException("URI syntax error for Naver Maps", e); // TODO
        }
    }

    private HttpHeaders getCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(GEOCODE_ACCESS_KEY_HEADER, this.accessKey);
        headers.add(GEOCODE_SECRET_KEY_HEADER, this.secretKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
