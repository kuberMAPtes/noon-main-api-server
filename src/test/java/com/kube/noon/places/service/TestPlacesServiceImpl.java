package com.kube.noon.places.service;

import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
class TestPlacesServiceImpl {

    @Autowired
    PlacesServiceImpl placesService;

    @DisplayName("장소명으로 검색")
    @Test
    void getPlaceList() {
        List<PlaceDto> results = this.placesService.getPlaceList("강남");

        assertThat(results).isNotEmpty();

        for (PlaceDto result : results) {
            log.info("도로명주소={}", result.getRoadAddress());
            log.info("위도={}", result.getX());
            log.info("경도={}", result.getY());
        }
    }

    @DisplayName("장소명으로 검색 - 존재하지 않는 장소를 검색할 경우 빈 List 반환")
    @ValueSource(strings = {
            "avsipndgpowipghrwpibjzx",
            "이런장소는이세상에없겠지전혀없겠지검색결과아예없겠지",
            "avshfibhpasfhb9q4htphwep",
            "이런장소가있다면 내 손에 장을 지진다."
    })
    @ParameterizedTest
    void getPlaceList_searchPlaceNotExists(String testCase) {
        List<PlaceDto> result = this.placesService.getPlaceList(testCase);
        assertThat(result).isEmpty();
    }

    @DisplayName("위도, 경도로 검색")
    @Test
    void getPlaceByLatLng() {
        final int latIdx = 0;
        final int lngIdx = 1;

        PlaceDto[] results = Stream.of(
                new double[] { 37.552277, 126.850703 },
                new double[] { 37.552429, 126.850795 },
                new double[] { 37.552292, 126.851102 }
        ).map((latLng) -> {
            try {
                return this.placesService.getPlaceByLatLng(latLng[latIdx], latLng[lngIdx]);
            } catch (PlaceNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).toArray((s) -> new PlaceDto[s]);

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

    @DisplayName("위도, 경도로 검색 - 유효하지 않은 위도, 경도")
    @CsvSource(value = {
            "1.4215314,128.512421583",
            "35.124152424,13.12584814",
            "12.2442513,16.3858142"
    }, delimiter = ',')
    @ParameterizedTest
    void getPlaceByLatLng_한국에_존재하지_않는_위도_및_경도(double latitude, double longitude) {
        assertThatExceptionOfType(PlaceNotFoundException.class)
                .isThrownBy(() -> this.placesService.getPlaceByLatLng(latitude, longitude));
    }
}