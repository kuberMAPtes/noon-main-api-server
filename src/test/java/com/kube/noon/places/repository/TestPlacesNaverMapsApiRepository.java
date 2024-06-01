package com.kube.noon.places.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kube.noon.places.domain.Place;
import com.kube.noon.places.exception.PlaceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
class TestPlacesNaverMapsApiRepository {

    @Autowired
    PlacesNaverMapsApiRepository placesRepository;

    @DisplayName("장소명으로 도로명주소, 위도, 경도 가져오기 테스트")
    @Test
    void findByPlaceName() {
        List<Place> results = this.placesRepository.findByPlaceName("강남");

        assertThat(results).isNotEmpty();

        for (Place result : results) {
            log.info("도로명주소={}", result.getRoadAddress());
            log.info("위도={}", result.getX());
            log.info("경도={}", result.getY());
        }
    }

    @DisplayName("위도, 경도 좌표로 도로명주소, 위도, 경도 가져오기 테스트")
    @Test
    void findByLatLng() {
        final int latIdx = 0;
        final int lngIdx = 1;

        Place[] results = Stream.of(
                new double[] { 37.552277, 126.850703 },
                new double[] { 37.552429, 126.850795 },
                new double[] { 37.552292, 126.851102 }
        ).map((latLng) -> {
            try {
                return this.placesRepository.findByLatLng(latLng[latIdx], latLng[lngIdx]);
            } catch (PlaceNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).toArray((s) -> new Place[s]);

        for (int i = 0; i < results.length; i++) {
            log.info("결과 {}", i + 1);
            log.info("도로명주소={}", results[i].getRoadAddress());
            log.info("위도={}", results[i].getX());
            log.info("경도={}\n", results[i].getY());
        }

        // 첫 번째 좌표와 두 번째 좌표는 같은 건물
        assertThat(results[0].getRoadAddress()).isEqualTo(results[1].getRoadAddress());

        // 세 번째 좌표는 다른 건물
        assertThat(results[2].getRoadAddress()).isNotEqualTo(results[0].getRoadAddress());
    }
}